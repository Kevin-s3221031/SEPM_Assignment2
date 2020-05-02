import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class ticketSystem {

	public static void main(String[] args)  {
		ticketSystem system = new ticketSystem();
		CSVManager CSV = system.new CSVManager();
		UserManager userManagement = system.new UserManager(CSV);
		TicketManager ticketManagement = system.new TicketManager(CSV);
		Boolean exit = false;
		loginStatus loggedIn = loginStatus.FAIL;
		Scanner scanner = new Scanner(System.in);
		do {
			while(loggedIn == loggedIn.FAIL) {
				System.out.println("CINCO TICKET MANAGER");
				System.out.println("--------------------");
				System.out.println("Please log in:");
				System.out.print("Enter a username: ");
				String loginUserName = scanner.next();
				System.out.print("Enter a password: ");
				String loginPassword = scanner.next();
				if(userManagement.logIn(loginUserName, loginPassword) == loginStatus.SUCCESSUSER ||
						userManagement.logIn(loginUserName, loginPassword) == loginStatus.SUCCESSTECH) {

					loggedIn = userManagement.logIn(loginUserName, loginPassword);
					if(loggedIn == loginStatus.SUCCESSUSER) {
						System.out.printf("%S has successfully logged in as User\r\n", loginUserName);
					}
					if(loggedIn == loginStatus.SUCCESSTECH) {
						System.out.printf("%S has successfully logged in as Technician\r\n", loginUserName);
					}

				}
				else if(userManagement.logIn(loginUserName, loginPassword) == loginStatus.FAIL){
					System.out.printf("Username does not exist, %S\r\n", loginUserName);
				}
				else {
					System.out.printf("There was a problem with the login service.\r\n");
				}
				enterToContinue();

			} 

			if(loggedIn == loginStatus.SUCCESSUSER) {
				System.out.printf("Current user: %S\r\n", userManagement.getLoggedInUser());
				System.out.println("Mode: User");
				System.out.println("CINCO TICKET MANAGER");
				System.out.println("--------------------");
				System.out.println("1. Create new ticket");
				System.out.println("2. View existing tickets");
				System.out.println("3. Reset password");
				System.out.println("4. Logout");
				System.out.println("5. Exit");
				System.out.print("Please make a selection: ");
				String choice = scanner.nextLine();
				switch(choice) {
				case "1":
					System.out.print("Enter your address: ");
					String address = scanner.nextLine();
					System.out.print("Enter your phone number: ");
					String phoneNumber = scanner.nextLine();
					System.out.print("Enter a short description of the problem: ");
					String description = scanner.nextLine();
					ticketSeverity severity = null;
					while(severity == null) {
						System.out.print("Please choose the severity of the problem (LOW, MEDIUM, HIGH): ");
						String rawSeverity = scanner.next();
						try {
							severity = ticketSeverity.valueOf(rawSeverity);
						}
						catch (Exception e) {
							System.out.print("Incompatible value. Try again.");
						}
						User currentUser = userManagement.getUser(userManagement.getLoggedInUser());

						if(ticketManagement.createTicket(CSV, currentUser.getFirstName(), currentUser.getLastName(), 
								currentUser.getstaffID(), currentUser.getEmail(), address, phoneNumber, severity, description)) {
							System.out.println("Successfully created new ticket.");
						}
						else {
							System.out.println("There was a problem creating a new ticket. Please try again.");
						}
					}
					enterToContinue();
					break;
				case "2":
					String staffID = userManagement.getUser(userManagement.getLoggedInUser()).getstaffID();
					if(ticketManagement.getTicketsOfUser(staffID).size() > 0) {
					int ticketCount = 1;
					for(Ticket thisTicket : ticketManagement.getTicketsOfUser(staffID)) {
						System.out.println("------------------------");
						System.out.println("\tTicket "+ ticketCount);
						System.out.println("------------------------");
						System.out.println("Date Logged: " + thisTicket.getCreatedDateTimeString());
						System.out.println("Severity: " + thisTicket.getSeverity());
						if(thisTicket.getStatus()) {
							System.out.println("Status: Open");
						}
						else {
							System.out.println("Status: Closed");
						}
						System.out.println("Issue Description: "+ thisTicket.getDescription());
						System.out.println("Contact Number: "+ thisTicket.getPhoneNumber());
						System.out.println("Address: "+ thisTicket.getAddress());
						System.out.println("Technician Assigned : "+ thisTicket.getAssignedTo());
						System.out.println("------------------------");
						++ticketCount;
						enterToContinue();
					}
					}
					else{
						System.out.println("No tickets logged by this user.");
						enterToContinue();
					}
					
					
					break;
				case "3":
					System.out.print("Enter old password: ");
					String oldPass = scanner.next();
					System.out.print("Enter a new password: ");
					String newPass = scanner.next();
					if(userManagement.resetUserPassword(CSV, userManagement.getLoggedInUser(), oldPass, newPass) == true) {
						System.out.println("Successfully changed password");
					}
					else {
						System.out.println("Incorrect old password");
					}
					enterToContinue();
					break;
				case "4":
					loggedIn = loginStatus.FAIL;
					System.out.println("You have successfully logged out.");
					enterToContinue();
					break;
				case "5":
					exit = true;
					scanner.close();
					break;
				default:
					// code block
				}

			}
			else {
				System.out.printf("Current user: %S\r\n", userManagement.getLoggedInUser());
				System.out.println("Mode: Technician");
				System.out.println("CINCO TICKET MANAGER");
				System.out.println("--------------------");
				System.out.println("Please make a selection:");
				System.out.println("1. View assigned tickets");
				System.out.println("2. Create new user");
				System.out.println("3. Reset password");
				System.out.println("4. Logout");
				System.out.println("5. Exit");

				String choice = "5";
				switch(choice) {
				case "1":

					break;
				case "2":
					System.out.print("Enter a staffID: ");
					String staffID = scanner.next();
					System.out.print("Enter an email address: ");
					String email = scanner.next();
					System.out.print("Enter your first name: ");
					String firstName = scanner.next();
					System.out.print("Enter your last name: ");
					String lastName = scanner.next();
					System.out.print("Enter a username: ");
					String userName = scanner.next();
					System.out.print("Enter a password: ");
					String password = scanner.next();
					if(!userManagement.createUser(CSV, userName, password, staffID, firstName, lastName, email)) {
						System.out.println("Staff ID already exists. Unable to create user.");
					}
					else {
						System.out.println("Success!");
					}
					break;
				case "3":
					System.out.print("Enter old password: ");
					String oldPass = scanner.next();
					System.out.print("Enter a new password: ");
					String newPass = scanner.next();
					if(userManagement.resetTechPassword(CSV, userManagement.getLoggedInUser(), oldPass, newPass) == true) {
						System.out.println("Successfully changed password");
					}
					else {
						System.out.println("Incorrect old password");
					}
					break;
				case "4":
					loggedIn = loginStatus.FAIL;
					break;
				case "5":
					exit = true;
					scanner.close();
					break;
				default:
					// code block
				}

			}
		}while(!exit);
		System.out.println("Goodbye!");
	}

	static public void enterToContinue() {
		System.out.println("Press enter to continue...");
		try{
			System.in.read();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public class User {
		protected String userName = "";
		protected String password = "";
		protected String staffID = "";
		protected String firstName = "";
		protected String lastName = "";
		protected String email = "";

		public User(String userName, String password, String staffID, String firstName, String lastName, String email) {
			this.userName = userName;
			this.password = password;
			this.staffID = staffID;
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
		}

		public String getUsername() {
			return userName;
		}

		public String getstaffID() {
			return staffID;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public String getEmail() {
			return email;
		}

		public boolean checkPassword(String passToCheck) {
			if(password.compareTo(passToCheck) == 0) {
				return true;
			}
			else {
				return false;
			}
		}

		public boolean changePassword(String oldPass, String newPass) {
			if(checkPassword(oldPass) == true) {
				return true;
			}
			else {
				return false;
			}
		}

	}

	public class Technician extends User {
		private boolean isLevelTwo = false;
		public Technician(String userName, String password, String staffID, String firstName, String lastName, String email, boolean isLevelTwo) {
			super(userName, password, staffID, firstName, lastName, email);
			this.userName = userName;
			this.password = password;
			this.staffID = staffID;
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
			this.isLevelTwo = isLevelTwo;
		}

		public boolean isLevelTwo() {
			return isLevelTwo;
		}
	}

	static enum ticketSeverity {
		LOW,
		MEDIUM,
		HIGH
	}
	static enum loginStatus {
		FAIL,
		SUCCESSTECH,
		SUCCESSUSER
	}

	public class Ticket {
		private String firstName = "";
		private String lastName = "";
		private String staffID = "";
		private String email = "";
		private String address = "";
		private String phoneNumber = "";
		private ticketSeverity severity = ticketSeverity.LOW;
		private String description = "";
		private boolean status = true;
		private String assignedTo = "";
		private LocalDateTime createdDateTime;

		public Ticket (String firstName, String lastName, String staffID, String email,
				String address, String phoneNumber, ticketSeverity severity, String description, 
				Boolean status, String assignedTo, LocalDateTime createdDateTime) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.staffID = staffID;
			this.email = email;
			this.address = address;
			this.phoneNumber = phoneNumber;
			this.severity = severity;
			this.description = description;
			this.status = status;
			this.assignedTo = assignedTo;
			this.createdDateTime = createdDateTime;
			System.out.println("Ticket constructed");
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public String getStaffID() {
			return staffID;
		}

		public String getEmail() {
			return email;
		}

		public String getAddress() {
			return address;
		}

		public String getPhoneNumber() {
			return phoneNumber;
		}

		public ticketSeverity getSeverity() {
			return severity;
		}

		public String getDescription() {
			return description;
		}

		public boolean getStatus() {
			return this.status;
		}

		public String getAssignedTo() {
			return this.assignedTo;
		}
		
		public String getCreatedDateTimeString() {
			DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			return createdDateTime.format(formatObj);
		}
		
		public void setStatus(boolean status) {
			this.status = status;
		}

		public void setSeverity(ticketSeverity severity) {
			this.severity = severity;
		}

		public void setAssignedTo(String assignedTo) {
			this.assignedTo = assignedTo;
		}
	}

	public class CSVManager {
		private String rootPath = ticketSystem.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		private String techniciansFileName = rootPath + "technicians.csv";
		private String usersFileName = rootPath + "users.csv";
		private String ticketsFileName = rootPath + "tickets.csv";

		private String[] initialTechnicianData = {"harrystyles,pass121,123451,Harry,Styles,harrystyles@cinco.com,false" + System.getProperty("line.separator"),
				"niallhoran,pass122,123452,Niall,Horan,niallhoran@cinco.com,false" + System.getProperty("line.separator"),
				"liampayne,pass123,123453,Liam,Payne,liampayne@cinco.com,false" + System.getProperty("line.separator"),
				"louistomlinson,pass124,123454,Louis,Tomlinson,louistomlinson@cinco.com,true" + System.getProperty("line.separator"),
				"zaynemalik,pass125,123456,Zayne,Malik,zaynemalik@cinco.com,true" + System.getProperty("line.separator"),};
		private String[] initialUserData = {
				"testuser,password,000001,Test,User,testuser@cinco.com" + System.getProperty("line.separator")
		};
		private String[] initialTicketsData = {};

		private ArrayList<User> userList = new ArrayList<User>();
		private ArrayList<Technician> techList = new ArrayList<Technician>();
		private ArrayList<Ticket> ticketList = new ArrayList<Ticket>();

		public CSVManager() {
			try {
				initialiseData();
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}

		public boolean techFileExists() {
			File tmpDir = new File(techniciansFileName);
			System.out.println(tmpDir);
			return tmpDir.exists();
		}

		public boolean userFileExists() {
			File tmpDir = new File(usersFileName);
			return tmpDir.exists();
		}

		public boolean ticketsFileExists() {
			File tmpDir = new File(ticketsFileName);
			return tmpDir.exists();
		}

		public void initialiseData() throws IOException {
			if(userFileExists() == false) {
				FileWriter csvWriter = new FileWriter(usersFileName);

				for (String stringTemp : initialUserData){
					csvWriter.append(stringTemp);
				}
				csvWriter.flush();
				csvWriter.close();
			}
			if(techFileExists() == false) {
				FileWriter csvWriter = new FileWriter(techniciansFileName);

				for (String stringTemp : initialTechnicianData){
					csvWriter.append(stringTemp);
				}
				csvWriter.flush();
				csvWriter.close();
			}

			if(ticketsFileExists() == false) {
				FileWriter csvWriter = new FileWriter(ticketsFileName);

				for (String stringTemp : initialTicketsData){
					csvWriter.append(stringTemp);
				}
				csvWriter.flush();
				csvWriter.close();
			}
		}

		public void UpdateUserFile(ArrayList<User> UserData) throws IOException {
			FileWriter csvWriter = new FileWriter(usersFileName, false);
			String temp = "";
			for (int i = 0; i < UserData.size(); i++) {
				User $tempUser = UserData.get(i);
				temp = $tempUser.userName + "," +
						$tempUser.password + "," +
						$tempUser.staffID + "," +
						$tempUser.firstName + "," +
						$tempUser.lastName + "," +
						$tempUser.email + System.getProperty("line.separator");
				csvWriter.append(temp);
			}
			csvWriter.flush();
			csvWriter.close();
		}

		public void UpdateTechnicianFile(ArrayList<Technician> TechnicianData) throws IOException {
			FileWriter csvWriter = new FileWriter(techniciansFileName,false);
			String temp = "";
			for (int i = 0; i < TechnicianData.size(); i++) {
				Technician $tempUser = TechnicianData.get(i);
				temp = $tempUser.userName + "," +
						$tempUser.password + "," +
						$tempUser.staffID + "," +
						$tempUser.firstName + "," +
						$tempUser.lastName + "," +
						$tempUser.email +
						$tempUser.isLevelTwo;
				csvWriter.append(temp);
			}
			csvWriter.flush();
			csvWriter.close();
		}

		public void UpdateTicketFile(ArrayList<Ticket> TicketData) throws IOException {
			FileWriter csvWriter = new FileWriter(ticketsFileName,false);
			String temp = "";
			for (int i = 0; i < TicketData.size(); i++) {
				System.out.println("Error?");
				Ticket $tempUser = TicketData.get(i);
				temp = $tempUser.firstName + "," +
						$tempUser.lastName + "," +
						$tempUser.staffID + "," +
						$tempUser.email +"," +
						$tempUser.address +"," +
						$tempUser.phoneNumber +"," +
						$tempUser.severity +"," +
						$tempUser.description +"," +
						$tempUser.status +"," +
						$tempUser.getAssignedTo() +"," +
						$tempUser.getCreatedDateTimeString();
				System.out.println("Error.");
				csvWriter.append(temp);
			}
			csvWriter.flush();
			csvWriter.close();
		}

		private void getUsersFromFile() throws IOException {
			String row;
			BufferedReader csvReader = new BufferedReader(new FileReader(usersFileName));
			while ((row = csvReader.readLine()) != null) {
				String[] data = row.split(",");
				userList.add(new User(data[0],data[1],data[2],data[3],data[4],data[5]));
			}
			csvReader.close();
		}

		private void getTechsFromFile() throws IOException {
			String row;
			BufferedReader csvReader = new BufferedReader(new FileReader(techniciansFileName));
			while ((row = csvReader.readLine()) != null) {
				String[] data = row.split(",");
				techList.add(new Technician(data[0],data[1],data[2],data[3],data[4],data[5],Boolean.parseBoolean(data[6])));
			}
			csvReader.close();
		}

		private void getTicketsFromFile() throws IOException {
			String row;
			BufferedReader csvReader = new BufferedReader(new FileReader(ticketsFileName));
			while ((row = csvReader.readLine()) != null) {
				String[] data = row.split(",");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); 
				LocalDateTime dateTime = LocalDateTime.parse(data[10], formatter);

				ticketList.add(new Ticket(data[0],data[1],data[2],data[3],data[4],data[5],ticketSeverity.valueOf(data[6].toUpperCase()),
						data[7],Boolean.parseBoolean(data[8]),data[9],dateTime));
			}
			csvReader.close();
		}

		public ArrayList<User> returnUsers() throws IOException{
			getUsersFromFile();
			return userList;
		}

		public ArrayList<Technician> returnTechs() throws IOException{
			getTechsFromFile();
			return techList;
		}

		public ArrayList<Ticket> returnTickets() throws IOException{
			getTicketsFromFile();
			return ticketList;
		}

	}

	public class UserManager {

		private String loggedInUser = null;
		private ArrayList<User> currentUsers;
		private ArrayList<Technician> currentTechs;
		public UserManager(CSVManager csv) {
			try {
				currentUsers = csv.returnUsers();
				currentTechs = csv.returnTechs();
			}
			catch(Exception e) {
				System.out.println("File read error " + e);
			}
		}

		public boolean createUser(CSVManager csv, String userName, String password, String staffID, String firstName,String lastName, String email) {
			Boolean iDExists = false;
			for (int i = 0; i < currentUsers.size(); i++) {
				if(staffID.compareTo(currentUsers.get(i).getstaffID()) == 0) {
					return false;
				}
			}
			if(!iDExists) {
				currentUsers.add(new User(userName, password, staffID, firstName, lastName, email));
				try {
					csv.UpdateUserFile(currentUsers);
				}
				catch(Exception e) {
					System.out.println("File read error " + e);
				}
				return true;
			}
			else {
				return false;
			}
		}

		public String getLoggedInUser() {
			return loggedInUser;
		}

		public User getUser(String userName) {
			for(User thisUser: currentUsers) {
				if(thisUser.getUsername().compareTo(userName) == 0) {
					return thisUser;
				}
			}
			return null;
		}

		public Technician getTech(String userName) {
			for(Technician thisTech: currentTechs) {
				if(thisTech.getUsername().compareTo(userName) == 0) {
					return thisTech;
				}
			}
			return null;
		}

		public loginStatus logIn(String username, String password) {
			for(int i = 0;i <currentUsers.size(); ++i) {
				if(username.compareTo(currentUsers.get(i).getUsername()) == 0){
					if(currentUsers.get(i).checkPassword(password)){
						loggedInUser = username;
						return loginStatus.SUCCESSUSER;
					}
				}
			}
			for(int i = 0;i <currentTechs.size(); ++i) {
				if(username.compareTo(currentTechs.get(i).getUsername()) == 0){
					if(currentTechs.get(i).checkPassword(password)){
						loggedInUser = username;
						return loginStatus.SUCCESSTECH;
					}
				}
			}
			return loginStatus.FAIL;
		}

		public boolean resetUserPassword(CSVManager csv, String username, String oldPass, String newPass) {
			for(int i = 0;i <currentUsers.size(); ++i) {
				if(username.compareTo(currentUsers.get(i).getUsername()) == 0){
					if(currentUsers.get(i).changePassword(oldPass, newPass)  ){
						try {
							csv.UpdateUserFile(currentUsers);
						}
						catch (Exception e){
							return false;
						}
						return true;
					}
					else {
						return false;
					}
				}
			}
			return false;
		}
		public boolean resetTechPassword(CSVManager csv, String username, String oldPass, String newPass) {
			for(int i = 0;i <currentTechs.size(); ++i) {
				if(username.compareTo(currentTechs.get(i).getUsername()) == 0){
					if(currentTechs.get(i).changePassword(oldPass, newPass)){
						try {
							csv.UpdateTechnicianFile(currentTechs);
						}
						catch (Exception e){
							return false;
						}
						return true;
					}
					else {
						return false;
					}
				}
			}
			return false;
		}
	}

	public class TicketManager {
		private ArrayList<Ticket> currentTickets;
		private ArrayList<Technician> currentTechs;
		Hashtable<Technician, Integer> ticketCount = new Hashtable<Technician, Integer>(); 

		public TicketManager(CSVManager csv) {
			try {
				currentTechs = csv.returnTechs();
				currentTickets = csv.returnTickets();
				for(int i = 0; i < currentTechs.size(); ++i) {
					int techTicketCount = 0;
					for(int j = 0; j < currentTickets.size(); ++j) {
						if(currentTickets.get(j).getAssignedTo().compareTo(currentTechs.get(i).getUsername()) == 0) {
							++techTicketCount;
						}
					}
					ticketCount.put(currentTechs.get(i), techTicketCount);
				}
			}
			catch(Exception e) {
				System.out.println("File read error " + e);
			}
		}

		public String getLowestLevelOne() {
			ArrayList<Technician> minList = new ArrayList<Technician>();
			Map.Entry<Technician, Integer> min = null;
			for (Map.Entry<Technician, Integer> entry : ticketCount.entrySet()) {
				if (entry.getKey().isLevelTwo() == false && (min == null || min.getValue() > entry.getValue())) {
					min = entry;
				}
			}
			if(min == null) {
				for(Technician thisTech : currentTechs) {
					if(thisTech.isLevelTwo() == false) {
						minList.add(thisTech);
					}
				}
			}
			else {
				minList.add(min.getKey());
				for (Map.Entry<Technician, Integer> entry : ticketCount.entrySet()) {
					if (min.getValue() == entry.getValue()) {
						minList.add(entry.getKey());
					}
				}
			}
			if(minList.size() == 1) {			
				return minList.get(0).getUsername();
			}
			else {
				Random r = new Random();
				int randomTech = r.nextInt(minList.size());
				return minList.get(randomTech).getUsername();
			}

		}

		public String getLowestLevelTwo() {
			ArrayList<Technician> minList = new ArrayList<Technician>();
			Map.Entry<Technician, Integer> min = null;
			for (Map.Entry<Technician, Integer> entry : ticketCount.entrySet()) {
				if (entry.getKey().isLevelTwo() == true && (min == null || min.getValue() > entry.getValue())) {
					min = entry;
				}
			}
			if(min == null) {
				for(Technician thisTech : currentTechs) {
					if(thisTech.isLevelTwo() == true) {
						minList.add(thisTech);
					}
				}
			}
			else {
				minList.add(min.getKey());
				for (Map.Entry<Technician, Integer> entry : ticketCount.entrySet()) {
					if (min.getValue() == entry.getValue()) {
						minList.add(entry.getKey());
					}
				}
			}
			if(minList.size() == 1) {			
				return minList.get(0).getUsername();
			}
			else {
				Random r = new Random();
				int randomTech = r.nextInt(minList.size());
				return minList.get(randomTech).getUsername();
			}

		}

		public boolean createTicket(CSVManager csv, String firstName, String lastName, String staffID,
				String email, String address, String phoneNumber, ticketSeverity severity,
				String description) {
			boolean status = true;
			String techAssigned = "";
			if(severity == ticketSeverity.HIGH) {
				System.out.println(getLowestLevelTwo());
				techAssigned = getLowestLevelTwo();
			}
			else {
				techAssigned = getLowestLevelOne();
			}
			try {
				currentTickets.add(new Ticket(firstName, lastName, staffID, email, address, phoneNumber,
						severity, description, status, techAssigned, LocalDateTime.now()));
				csv.UpdateTicketFile(currentTickets);
				return true;
			}
			catch(Exception e) {
				System.out.print(e);
				return false;
			}
		}
		public ArrayList<Ticket> getTicketsOfUser(String staffID){
			ArrayList<Ticket> ticketsForUser = new ArrayList<Ticket>();
			for (Ticket thisTicket : currentTickets) {
				if(thisTicket.getStaffID().compareTo(staffID) == 0) {
					ticketsForUser.add(thisTicket);
				}
			}
			return ticketsForUser;
		}
		public ArrayList<Ticket> getTicketsAssignedToTech(String userName){
			ArrayList<Ticket> ticketsForTech = new ArrayList<Ticket>();
			for (Ticket thisTicket : currentTickets) {
				if(thisTicket.getAssignedTo().compareTo(userName) == 0) {
					ticketsForTech.add(thisTicket);
				}
			}
			return ticketsForTech;
		}
	}
}

