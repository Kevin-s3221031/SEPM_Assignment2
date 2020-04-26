import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

public class ticketSystem {

	public static void main(String[] args)  {
		ticketSystem system = new ticketSystem();
		CSVManager CSV = system.new CSVManager();
		UserManager userManagement = system.new UserManager(CSV);
		Boolean exit = false;
		do {

			Scanner scanner = new Scanner(System.in);
		System.out.println("CINCO TICKET MANAGER");
		System.out.println("--------------------");
		System.out.println("1. Create new User");
		System.out.println("2. Login");
		System.out.println("6. Exit");
		System.out.print("What would you like to do? ");
		String choice = scanner.next();
		
		switch(choice) {
		  case "1":
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
		  case "2":
			  System.out.print("Enter a username: ");
			  String loginUserName = scanner.next();
			  System.out.print("Enter a password: ");
			  String loginPassword = scanner.next();
			  break;
		  case "6":
			  exit = true;
			  break;
		  default:
		    // code block
		}
		}while(!exit);
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
		
		public boolean checkPassword(String passToCheck) {
			if(password.compareTo(passToCheck) == 0) {
				return true;
			}
			else {
				return false;
			}
		}
		
		public boolean resetPassword(String oldPass, String newPass) {
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
		
		public Ticket (String firstName, String lastName, String staffID, String email,
				String address, String phoneNumber, ticketSeverity severity, String description) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.staffID = staffID;
			this.email = email;
			this.address = address;
			this.phoneNumber = phoneNumber;
			this.severity = severity;
			this.description = description;
		}
		
		public Ticket (String firstName, String lastName, String staffID, String email,
				String address, String phoneNumber, ticketSeverity severity, String description, Boolean status) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.staffID = staffID;
			this.email = email;
			this.address = address;
			this.phoneNumber = phoneNumber;
			this.severity = severity;
			this.description = description;
			this.status = status;
		}
		
		public String getFirstName() {
			return firstName;
		}
		
		public String getLastName() {
			return lastName;
		}
		
		public String staffID() {
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
		
		public void setStatus(boolean status) {
			this.status = status;
		}
		
		public void setSeverity(ticketSeverity severity) {
			this.severity = severity;
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
	        	Ticket $tempUser = TicketData.get(i);
	            temp = $tempUser.firstName + "," +
	            		$tempUser.lastName + "," +
	            		$tempUser.staffID + "," +
	            		$tempUser.email +"," +
	            		$tempUser.address +"," +
	            		$tempUser.phoneNumber +"," +
	            		$tempUser.severity +"," +
	            		$tempUser.description +"," +
	            		$tempUser.status;
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
	            ticketList.add(new Ticket(data[0],data[1],data[2],data[3],data[4],data[5],ticketSeverity.valueOf(data[6].toUpperCase()),data[7],Boolean.parseBoolean(data[8])));
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
		private User loggedInUser = null;
		private ArrayList<User> currentUsers;
		public UserManager(CSVManager csv) {
			try {
				currentUsers = csv.returnUsers();
			}
			catch(Exception e) {
				System.out.println("File read error" + e);
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
					System.out.println("File read error" + e);
				}
				return true;
			}
			else {
				return false;
			}
				

		}
	}
}

