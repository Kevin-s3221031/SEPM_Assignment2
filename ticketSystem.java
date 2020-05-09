import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ticketSystem {

	public static void main(String[] args)  {
		ticketSystem system = new ticketSystem();
		CSVManager CSV = system.new CSVManager();
		UserManager userManagement = system.new UserManager(CSV);
		TicketManager ticketManagement = system.new TicketManager(CSV);
		Boolean exit = false;
		loginStatus loggedIn = loginStatus.FAIL;

		do {
			while(loggedIn == loginStatus.FAIL) {
				System.out.println("CINCO TICKET MANAGER");
				System.out.println("--------------------");
				System.out.println("Please log in:");

				String loginUserName = ""; 
				while(loginUserName =="") {
					loginUserName = getUserInput("Enter a username");
					if (loginUserName == "") {
						System.out.println("Input was empty. Try again.");
					}
				}
				String loginPassword = getUserInput("Enter a password");

				if(userManagement.logIn(loginUserName, loginPassword) == loginStatus.SUCCESSUSER ||
						userManagement.logIn(loginUserName, loginPassword) == loginStatus.SUCCESSTECH) {

					loggedIn = userManagement.logIn(loginUserName, loginPassword);
					if(loggedIn == loginStatus.SUCCESSUSER) {
						System.out.printf("%S has successfully logged in as User.\r\n", loginUserName);
					}
					if(loggedIn == loginStatus.SUCCESSTECH) {
						System.out.printf("%S has successfully logged in as Technician.\r\n", loginUserName);
					}

				}
				else if(userManagement.logIn(loginUserName, loginPassword) == loginStatus.FAIL){
					System.out.println("Username or password incorrect.");
				}
				else {
					System.out.println("There was a problem with the login service.");
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
				String choice = getUserInput("Please make a selection");

				switch(choice) {
				case "1":
					System.out.println("Warning: Empty input will cancel the current action.");
					String address = getUserInput("Enter your address");
					if(address.compareTo("") == 0) {
						System.out.println("User action cancelled.");
						enterToContinue();
						break;
					}
					String phoneNumber = getUserInput("Enter your phone number");
					if(phoneNumber.compareTo("") == 0) {
						System.out.println("User action cancelled.");
						enterToContinue();
						break;
					}
					String description = getUserInput("Enter a short description of the problem");
					if(description.compareTo("") == 0) {
						System.out.println("User action cancelled.");
						enterToContinue();
						break;
					}
					ticketSeverity severity = null;
					while(severity == null) {
						String rawSeverity = getUserInput("Please choose the severity of the problem (LOW, MEDIUM, HIGH)");
						try {
							severity = ticketSeverity.valueOf(rawSeverity.toUpperCase());
						}
						catch (Exception e) {
							System.out.println("Incorrect response. Try again.");
						}
					}

					User currentUser = userManagement.getUser(userManagement.getLoggedInUser());

					if(ticketManagement.createTicket(currentUser.getFirstName(), currentUser.getLastName(), 
							currentUser.getstaffID(), currentUser.getEmail(), address, phoneNumber, severity, description)) {
						System.out.println("Successfully created new ticket.");
					}
					else {
						System.out.println("There was a problem creating a new ticket. Please try again.");
					}

					enterToContinue();
					break;
				case "2":
					String staffID = userManagement.getUser(userManagement.getLoggedInUser()).getstaffID();
					ArrayList<Ticket> ticketsForID = ticketManagement.getTicketsOfUser(staffID);
					if(ticketsForID.size() > 0) {
						for(int i = 0; i < ticketsForID.size(); ++i) {
							System.out.println("------------------------");
							System.out.println("\tTicket "+ (i + 1));
							System.out.println("------------------------");
							System.out.println("Date Logged: " + ticketsForID.get(i).getCreatedDateTimeString());
							System.out.println("Severity: " + ticketsForID.get(i).getSeverity());
							if(ticketsForID.get(i).getStatus()) {
								System.out.println("Status: Open");
							}
							else {
								System.out.println("Status: Closed");
							}
							System.out.println("Issue Description: "+ ticketsForID.get(i).getDescription());
							System.out.println("Contact Number: "+ ticketsForID.get(i).getPhoneNumber());
							System.out.println("Address: "+ ticketsForID.get(i).getAddress());
							System.out.println("Technician Assigned : "+ ticketsForID.get(i).getAssignedTo());
							System.out.println("------------------------");
							System.out.println("Technician Updates");
							System.out.println("------------------------");
							if(ticketsForID.get(i).getNotes().length == 0) {
								System.out.println("No technician updates.");
								System.out.println("------------------------");
							}
							else {
								for(String thisNote : ticketsForID.get(i).getNotes()) {
									System.out.println("Added: " + thisNote.substring(0,19));
									System.out.println("Note: " + thisNote.substring(20));
									System.out.println("------------------------");
								}
							}
							enterToContinue();
						}
					}
					else{
						System.out.println("No tickets logged by this user.");
						enterToContinue();
					}


					break;
				case "3":
					String oldPass = getUserInput("Enter old password");
					String newPass = getUserInput("Enter a new password");
					if(userManagement.resetUserPassword(userManagement.getLoggedInUser(), oldPass, newPass) == true) {
						System.out.println("Successfully changed password.");
					}
					else {
						System.out.println("Incorrect old password.");
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
					break;
				default:
					System.out.println("Please enter a valid option.");
					enterToContinue();
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

				String choice = getUserInput("Please make a selection");

				switch(choice) {
				case "1":
					String thisUsername = userManagement.loggedInUser;
					ArrayList<Ticket> ticketsForTech = ticketManagement.getTicketsAssignedToTech(thisUsername);
					if(ticketsForTech.size() > 0) {
						for(int i = 0; i < ticketsForTech.size(); ++i) {
							String editMenuOption = "";
							boolean exitSubMenu = false;
							while(!exitSubMenu) {
								System.out.println("------------------------");
								System.out.println("\tTicket "+ (i + 1));
								System.out.println("------------------------");
								System.out.println("Date Logged: " + ticketsForTech.get(i).getCreatedDateTimeString());
								System.out.println("Severity: " + ticketsForTech.get(i).getSeverity());
								if(ticketsForTech.get(i).getStatus()) {
									System.out.println("Status: Open");
								}
								else {
									System.out.println("Status: Closed");
								}
								System.out.println("Issue Description: "+ ticketsForTech.get(i).getDescription());
								System.out.println("Contact Number: "+ ticketsForTech.get(i).getPhoneNumber());
								System.out.println("Address: "+ ticketsForTech.get(i).getAddress());
								System.out.println("Submitted by : "+ ticketsForTech.get(i).getFirstName() + " " + ticketsForTech.get(i).getLastName());
								System.out.println("Assigned To: "+ ticketsForTech.get(i).getAssignedTo());
								System.out.println("------------------------");
								System.out.println("Technician Updates");
								System.out.println("------------------------");
								if(ticketsForTech.get(i).getNotes().length == 0) {
									System.out.println("No technician updates.");
									System.out.println("------------------------");
								}
								else {
									for(String thisNote : ticketsForTech.get(i).getNotes()) {
										System.out.println("Added: " + thisNote.substring(0,19));
										System.out.println("Note: " + thisNote.substring(20));
										System.out.println("------------------------");
									}
								}
								System.out.println("Would you like to:");
								System.out.println("1. Add an update to this ticket");
								System.out.println("2. Change the status of this ticket");
								System.out.println("3. Change the severity of this ticket");
								if(i == ticketsForTech.size() - 1) {
									System.out.println("4. Go back to main menu");
								}
								else {
									System.out.println("4. View next ticket");
								}


								editMenuOption = getUserInput("Please select an option");
								switch(editMenuOption) {
								case "1":
									if(ticketsForTech.get(i).getStatus() == false) {
										System.out.println("This ticket is closed and cannot be edited.");
										enterToContinue();
										break;
									}
									String newNote = "";
									System.out.println("Warning: empty input will cancel user action.");
									newNote = getUserInput("Please enter the ticket update");
									if(newNote.compareTo("") == 0) {
										System.out.println("User action cancelled.");
									}
									else{
										ticketManagement.addTechNote(ticketsForTech.get(i).getCreatedDateTime(), newNote);
										System.out.println("Ticket update added to ticket.");
									}
									enterToContinue();
									break;
								case "2":
									if(ticketsForTech.get(i).getStatus() == false){
										System.out.println("This ticket is already closed and cannot be re-opened.");
									}
									else {
									ticketManagement.changeStatus(ticketsForTech.get(i).getCreatedDateTime(), false);
									System.out.println("This ticket is now closed.");
									}
									enterToContinue();
									break;
								case "3":
									if(ticketsForTech.get(i).getStatus() == false) {
										System.out.println("This ticket is closed and cannot be edited.");
										enterToContinue();
										break;
									}
									System.out.println("Warning: Empty input will cancel operation.");
									ticketSeverity severity = null;
									String rawSeverity = getUserInput("Please choose the severity of the problem (LOW, MEDIUM, HIGH)");
									try {
										severity = ticketSeverity.valueOf(rawSeverity.toUpperCase());
										if(ticketsForTech.get(i).getSeverity() == severity) {
											System.out.print("New severity matches old severity.");
										}
										else {
											System.out.println("change severity being called");
											ticketManagement.changeSeverity(ticketsForTech.get(i).getCreatedDateTime(), severity);
											System.out.println("change severity has been called");
										}
									}
									catch (Exception e) {
										System.out.println("Invalid input.");
									}
									enterToContinue();
									break;
								case "4":
									exitSubMenu = true;
									break;
								default:
									System.out.println("Invalid selection. Try again.");
									enterToContinue();
									break;
								}
							}
						}
					}
					else{
						System.out.println("No tickets assigned to this technician.");
						enterToContinue();
					}

					break;
				case "2":
					System.out.println("Warning: Empty input will cancel current operation.");
					String staffID = getUserInput("Enter a staffID");
					if(staffID.compareTo("") == 0) {
						System.out.println("User action cancelled.");
						enterToContinue();
						break;
					}
					String email = getUserInput("Enter an email address");
					if(email.compareTo("") == 0) {
						System.out.println("User action cancelled.");
						enterToContinue();
						break;
					}
					String firstName = getUserInput("Enter users first name");
					if(firstName.compareTo("") == 0) {
						System.out.println("User action cancelled.");
						enterToContinue();
						break;
					}
					String lastName = getUserInput("Enter user last name");
					if(lastName.compareTo("") == 0) {
						System.out.println("User action cancelled.");
						enterToContinue();
						break;
					}
					String userName = getUserInput("Enter user username");
					if(userName.compareTo("") == 0) {
						System.out.println("User action cancelled.");
						enterToContinue();
						break;
					}
					String password = getUserInput("Enter password for new user");
					if(password.compareTo("") == 0) {
						System.out.println("User action cancelled.");
						enterToContinue();
						break;
					}

					if(!userManagement.createUser(userName, password, staffID, firstName, lastName, email)) {
						System.out.println("Problem creating user.");
					}
					else {
						System.out.println("Successfully created new user.");
					}
					enterToContinue();
					break;
				case "3":
					String oldPass = getUserInput("Enter old password");
					String newPass = getUserInput("Enter a new password");

					if(userManagement.resetTechPassword(userManagement.getLoggedInUser(), oldPass, newPass) == true) {
						System.out.println("Successfully changed password.");
					}
					else {
						System.out.println("Incorrect old password.");
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
					break;
				default:
					System.out.println("Please enter a valid option.");
					enterToContinue();
				}

			}
		}while(!exit);
		System.out.println("Thank you for using the Cinco Ticket Manager.");
		System.out.println("Goodbye!");
	}


	static public String getUserInput(String prompt) {
		BufferedReader bufferedRead = new BufferedReader(new InputStreamReader(System.in));
		String textFromUser = "";
		if("Press enter to continue...".compareTo(prompt) != 0) {
			System.out.print(prompt + ": ");
		}
		else {
			System.out.print(prompt);
		}
		try {
			textFromUser = bufferedRead.readLine();

		}
		catch (Exception e) {
			System.out.println("Catastrophic error in BufferRead. See stacktrace:");
			System.out.println(e.toString());
			System.exit(1);
		}

		return textFromUser;
	}

	static public void enterToContinue() {
		String output = getUserInput("Press enter to continue...");
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
				password = newPass;
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
		private String notes = "";

		public Ticket (String firstName, String lastName, String staffID, String email,
				String address, String phoneNumber, ticketSeverity severity, String description, 
				Boolean status, String assignedTo, LocalDateTime createdDateTime, String notes) {
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
			this.notes = notes;
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

		public LocalDateTime getCreatedDateTime() {
			return createdDateTime;
		}

		public String getCreatedDateTimeString() {
			DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			return createdDateTime.format(formatObj);
		}

		public String[] getNotes() {
			if(notes.compareTo("") == 0) {
				return new String[0];
			}
			ArrayList<String> arrayListOfNotes = new ArrayList<String>();
			Collections.addAll(arrayListOfNotes, notes.split("`"));
			arrayListOfNotes.removeAll(Arrays.asList("", null));
			String[] returnStringArray = arrayListOfNotes.toArray(new String[0]);
			return returnStringArray;
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

		public void addNote(String newNote) {
			DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newNoteTime = LocalDateTime.now().format(formatObj); 
			notes += (newNoteTime+"$"+newNote + "`");
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
				getUsersFromFile();
				getTechsFromFile();
				getTicketsFromFile();
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}

		private boolean techFileExists() {
			File tmpDir = new File(techniciansFileName);
			System.out.println(tmpDir);
			return tmpDir.exists();
		}

		private boolean userFileExists() {
			File tmpDir = new File(usersFileName);
			return tmpDir.exists();
		}

		private boolean ticketsFileExists() {
			File tmpDir = new File(ticketsFileName);
			return tmpDir.exists();
		}

		private void initialiseData(){
			try {
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
			catch(Exception e) {
				System.out.println("Catastrophic failure initialising data. See below: ");
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}

		private boolean UpdateUserFile(ArrayList<User> UserData) {
			try {
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
				return true;
			}
			catch(Exception e) {
				return false;
			}
		}

		private boolean UpdateTechnicianFile(ArrayList<Technician> TechnicianData) {
			try {
				FileWriter csvWriter = new FileWriter(techniciansFileName,false);
				String temp = "";
				for (int i = 0; i < TechnicianData.size(); i++) {
					Technician $tempUser = TechnicianData.get(i);
					temp = $tempUser.userName + "," +
							$tempUser.password + "," +
							$tempUser.staffID + "," +
							$tempUser.firstName + "," +
							$tempUser.lastName + "," +
							$tempUser.email + "," +
							$tempUser.isLevelTwo + System.getProperty("line.separator");
					csvWriter.append(temp);
				}

				csvWriter.flush();
				csvWriter.close();
				return true;
			}
			catch (Exception e) {
				return false;
			}
		}

		private boolean UpdateTicketFile(ArrayList<Ticket> TicketData) {
			try {
				FileWriter csvWriter = new FileWriter(ticketsFileName,false);
				String temp = "";
				for (int i = 0; i < TicketData.size(); i++) {
					Ticket ticketToWrite = TicketData.get(i);
					temp = ticketToWrite.firstName + "," +
							ticketToWrite.lastName + "," +
							ticketToWrite.staffID + "," +
							ticketToWrite.email +"," +
							ticketToWrite.address +"," +
							ticketToWrite.phoneNumber +"," +
							ticketToWrite.severity +"," +
							ticketToWrite.description +"," +
							ticketToWrite.status +"," +
							ticketToWrite.getAssignedTo() +"," +
							ticketToWrite.getCreatedDateTimeString() + ",";
					for(String thisNote : ticketToWrite.getNotes()) {
						temp += thisNote + "`"; 
					}
					temp += System.getProperty("line.separator");
					csvWriter.append(temp);
				}
				csvWriter.flush();
				csvWriter.close();
				return true;
			}
			catch(Exception e) {
				return false;
			}
		}

		private void getUsersFromFile() {
			try {
				String row;
				BufferedReader csvReader = new BufferedReader(new FileReader(usersFileName));
				while ((row = csvReader.readLine()) != null) {
					String[] data = row.split(",");
					userList.add(new User(data[0],data[1],data[2],data[3],data[4],data[5]));
				}
				csvReader.close();
			}
			catch(Exception e) {
				System.out.println("Catastrophic failure reading from Users file. See below: ");
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}

		private void getTechsFromFile(){
			try {
				String row;
				BufferedReader csvReader = new BufferedReader(new FileReader(techniciansFileName));
				while ((row = csvReader.readLine()) != null) {
					String[] data = row.split(",");
					techList.add(new Technician(data[0],data[1],data[2],data[3],data[4],data[5],Boolean.parseBoolean(data[6])));
				}
				csvReader.close();
			}
			catch(Exception e) {
				System.out.println("Catastrophic failure reading from Technicians file. See below: ");
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}

		private void getTicketsFromFile() {
			String row;
			try {
				BufferedReader csvReader = new BufferedReader(new FileReader(ticketsFileName));
				while ((row = csvReader.readLine()) != null) {
					String[] data = row.split(",");
					String notesToAdd = "";
					if(data.length == 12) {
						notesToAdd  = data[11];
					}
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); 
					LocalDateTime fileDateTime = LocalDateTime.parse(data[10], formatter);
					Boolean statusUpdate = false;
					if(fileDateTime.compareTo(LocalDateTime.now().minusDays(7)) >= 0) {
						statusUpdate = true;
					}
					ticketList.add(new Ticket(data[0],data[1],data[2],data[3],data[4],data[5],ticketSeverity.valueOf(data[6].toUpperCase()),
							data[7],statusUpdate,data[9],fileDateTime,notesToAdd));
				}
				csvReader.close();
			}
			catch(Exception e) {
				System.out.println("Catastrophic failure reading from Tickets file. See below: ");
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}

		public void updateUserList(User userUpdate, boolean update) {
			if(update == false) {
				userList.add(userUpdate);
			}
			else {
				for(int i = 0; i < userList.size(); ++i) {
					if(userList.get(i).getUsername().compareTo(userUpdate.getUsername()) == 0) {
						userList.remove(i);
					}
				}
				userList.add(userUpdate);
			}
			UpdateUserFile(userList);
		}

		public void updateTicketList(Ticket newTicket, boolean update) {			
			if(update == false) {
				ticketList.add(newTicket);
			}
			else {
				for(int i = 0; i < ticketList.size(); ++i) {
					if(ticketList.get(i).getCreatedDateTimeString().compareTo(newTicket.getCreatedDateTimeString()) == 0) {
						ticketList.remove(i);
					}
				}
				ticketList.add(newTicket);
			}
			UpdateTicketFile(ticketList);
		}

		public void updateTechList(Technician techUpdate, boolean update) {
			if(update == false) {
				techList.add(techUpdate);
			}
			else {
				for(int i = 0; i < userList.size(); ++i) {
					if(techList.get(i).getUsername().compareTo(techUpdate.getUsername()) == 0) {
						techList.remove(i);
					}
				}
				techList.add(techUpdate);
			}
			UpdateTechnicianFile(techList);
		}		
		public ArrayList<User> getAllUsers(){
			return userList;
		}

		public ArrayList<Technician> getAllTechs(){
			return techList;
		}

		public ArrayList<Technician> getLevelOneTechs(){
			ArrayList<Technician> levelOnes = new ArrayList<Technician>();
			for(Technician techToCheck : techList) {
				if(techToCheck.isLevelTwo == false) {
					levelOnes.add(techToCheck);
				}
			}
			return levelOnes;
		}

		public ArrayList<Technician> getLevelTwoTechs(){
			ArrayList<Technician> levelTwos = new ArrayList<Technician>();
			for(Technician techToCheck : techList) {
				if(techToCheck.isLevelTwo == true) {
					levelTwos.add(techToCheck);
				}
			}
			return levelTwos;
		}

		public ArrayList<Ticket> getAllTickets(){
			return ticketList;
		}

		public boolean save() {
			if(!UpdateUserFile(userList)) {
				return false;
			}
			if(!UpdateTechnicianFile(techList)) {
				return  false;
			}
			if(!UpdateTicketFile(ticketList)) {
				return false;
			}
			return true;
		}

	}

	public class UserManager {
		private CSVManager csv;
		private String loggedInUser = null;

		public UserManager(CSVManager csv) {
			this.csv = csv;
		}

		public boolean createUser(String userName, String password, String staffID, String firstName,String lastName, String email) {
			for (User thisUser : csv.getAllUsers()) {
				if(staffID.compareTo(thisUser.getstaffID()) == 0 ||
						userName.compareTo(thisUser.getUsername()) == 0) {
					return false;
				}
			}
			csv.updateUserList(new User(userName, password, staffID, firstName, lastName, email), false);
			return true;
		}

		public String getLoggedInUser() {
			return loggedInUser;
		}

		public User getUser(String userName) {
			ArrayList<User> currentUsers = csv.getAllUsers();
			for(User thisUser: currentUsers) {
				if(thisUser.getUsername().compareTo(userName) == 0) {
					return thisUser;
				}
			}
			return null;
		}

		public Technician getTech(String userName) {
			for(Technician thisTech: csv.getAllTechs()) {
				if(thisTech.getUsername().compareTo(userName) == 0) {
					return thisTech;
				}
			}
			return null;
		}

		public loginStatus logIn(String username, String password) {
			for(User thisUser : csv.getAllUsers()) {
				if(username.compareTo(thisUser.getUsername()) == 0){
					if(thisUser.checkPassword(password)){
						loggedInUser = username;
						return loginStatus.SUCCESSUSER;
					}
				}
			}
			for(Technician thisTech : csv.getAllTechs()) {
				if(username.compareTo(thisTech.getUsername()) == 0){
					if(thisTech.checkPassword(password)){
						loggedInUser = username;
						return loginStatus.SUCCESSTECH;
					}
				}
			}
			return loginStatus.FAIL;
		}

		public boolean resetUserPassword(String username, String oldPass, String newPass) {
			for(User thisUser : csv.getAllUsers()) {
				if(username.compareTo(thisUser.getUsername()) == 0){
					if(thisUser.changePassword(oldPass, newPass)  ){
						csv.updateUserList(thisUser, true);
						return true;
					}
					else {
						return false;
					}
				}
			}
			return false;
		}

		public boolean resetTechPassword(String username, String oldPass, String newPass) {
			for(Technician thisTech : csv.getAllTechs()) {
				if(username.compareTo(thisTech.getUsername()) == 0){
					if(thisTech.changePassword(oldPass, newPass)){
						csv.updateTechList(thisTech, true);
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
		private CSVManager csv;

		public TicketManager(CSVManager csv) {
			this.csv = csv;
		}

		public String getLowestLevelOne() {
			ArrayList<Technician> levelOnes = csv.getLevelOneTechs(); 
			ArrayList<Integer> ticketCount = new ArrayList<Integer>();
			ArrayList<Technician> minList = new ArrayList<Technician>();

			for(int i = 0; i < levelOnes.size(); ++i) {
				int entryTicketCount = 0;
				for(Ticket ticketBeingChecked : csv.getAllTickets()) {
					if(levelOnes.get(i).getUsername().compareTo(ticketBeingChecked.getAssignedTo()) == 0) {
						++entryTicketCount;
					}
				}
				ticketCount.add(entryTicketCount);
			}

			int firstLowestTicketCount = ticketCount.indexOf(Collections.min(ticketCount));
			int countDuplicates = Collections.frequency(ticketCount, firstLowestTicketCount); 
			if( countDuplicates == 1) {
				return levelOnes.get(firstLowestTicketCount).getUsername();
			}
			else {
				for(int i = 0; i < ticketCount.size(); ++i) {
					if(ticketCount.get(i) == firstLowestTicketCount) {
						minList.add(levelOnes.get(i));
					}
				}
			}
			Random r = new Random();
			int randomTech = r.nextInt(minList.size());
			return minList.get(randomTech).getUsername();


		}

		public String getLowestLevelTwo() {
			ArrayList<Technician> levelTwos = csv.getLevelTwoTechs(); 
			ArrayList<Integer> ticketCount = new ArrayList<Integer>();
			ArrayList<Technician> minList = new ArrayList<Technician>();

			for(int i = 0; i < levelTwos.size(); ++i) {
				int entryTicketCount = 0;
				for(Ticket ticketBeingChecked : csv.getAllTickets()) {
					if(levelTwos.get(i).getUsername().compareTo(ticketBeingChecked.getAssignedTo()) == 0) {
						++entryTicketCount;
					}
				}
				ticketCount.add(entryTicketCount);
			}

			int firstLowestTicketCount = ticketCount.indexOf(Collections.min(ticketCount));
			int countDuplicates = Collections.frequency(ticketCount, firstLowestTicketCount); 
			if( countDuplicates == 1) {
				return levelTwos.get(firstLowestTicketCount).getUsername();
			}
			else {
				for(int i = 0; i < ticketCount.size(); ++i) {
					if(ticketCount.get(i) == firstLowestTicketCount) {
						minList.add(levelTwos.get(i));
					}
				}
			}
			Random r = new Random();
			int randomTech = r.nextInt(minList.size());
			return minList.get(randomTech).getUsername();


		}

		public boolean createTicket(String firstName, String lastName, String staffID,
				String email, String address, String phoneNumber, ticketSeverity severity,
				String description) {
			ArrayList<Ticket> currentTickets = csv.getAllTickets();
			boolean status = true;
			String techAssigned = "";
			if(severity == ticketSeverity.HIGH) {
				techAssigned = getLowestLevelTwo();
			}
			else {
				techAssigned = getLowestLevelOne();
			}

			if(currentTickets.add(new Ticket(firstName, lastName, staffID, email, address, phoneNumber,
					severity, description, status, techAssigned, LocalDateTime.now(),"")) == true){
				csv.UpdateTicketFile(currentTickets);
				return true;
			}
			else {
				return false;
			}
		}
		public ArrayList<Ticket> getTicketsOfUser(String staffID){
			ArrayList<Ticket> ticketsForUser = new ArrayList<Ticket>();
			for (Ticket thisTicket : csv.getAllTickets()) {
				if(thisTicket.getStaffID().compareTo(staffID) == 0) {
					ticketsForUser.add(thisTicket);
				}
			}
			return ticketsForUser;
		}
		public ArrayList<Ticket> getTicketsAssignedToTech(String userName){
			ArrayList<Ticket> ticketsForTech = new ArrayList<Ticket>();
			for (Ticket thisTicket : csv.getAllTickets()) {
				if(thisTicket.getAssignedTo().compareTo(userName) == 0) {
					ticketsForTech.add(thisTicket);
				}
			}
			return ticketsForTech;
		}

		public Ticket getTicket(LocalDateTime createdDate) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); 
			String formattedDateTime = createdDate.format(formatter);
			for(Ticket ticketBeingChecked : csv.getAllTickets()) {
				if(ticketBeingChecked.getCreatedDateTimeString().compareTo(formattedDateTime) == 0) {
					return ticketBeingChecked;
				}
			}
			return null;
		}

		public void addTechNote(LocalDateTime createdDate, String techNote) {
			Ticket addNoteTo = getTicket(createdDate);
			addNoteTo.addNote(techNote);
			csv.updateTicketList(addNoteTo, true);
		}

		public void changeStatus(LocalDateTime createdDate, Boolean newStatus) {
			Ticket changeStatusOf = getTicket(createdDate);
			changeStatusOf.setStatus(newStatus);
			csv.updateTicketList(changeStatusOf, true);
		}
		
		public void changeSeverity(LocalDateTime createdDate, ticketSeverity newSeverity) {
			Ticket changeSeverityOf = getTicket(createdDate);
			changeSeverityOf.setSeverity(newSeverity);
			if(changeSeverityOf.getSeverity() == ticketSeverity.HIGH) {
				changeSeverityOf.setAssignedTo(getLowestLevelTwo());
			}
			else {
				changeSeverityOf.setAssignedTo(getLowestLevelOne());
			}
			csv.updateTicketList(changeSeverityOf, true);
		}
	}
}

