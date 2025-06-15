import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;//for formatting date and converting in to strings
import java.time.temporal.ChronoUnit;//for adding date
import java.util.Scanner;

public class Library_system {
    // 1. CONSTANTS AND “DATABASE” ARRAYS
    static final int MAX_USERS   = 100;
    static final int MAX_BOOKS   = 1000;
    static final int MAX_HISTORY = 5000;

    // users.dat fields
    // Users all attributes
    static String[] regNos     = new String[MAX_USERS];
    static String[] usernames  = new String[MAX_USERS];
    static String[] passwords  = new String[MAX_USERS];
    static String[] roles      = new String[MAX_USERS];   // "admin" or "student"
    static int userCount = 0;

    // books.dat fields
    static String[] bookIDs    = new String[MAX_BOOKS];
    static String[] titles     = new String[MAX_BOOKS];
    static String[] authors    = new String[MAX_BOOKS];
    static String[] bookCategory = new String[MAX_BOOKS];
    static String[] statuses   = new String[MAX_BOOKS];   // "available" or "issued"
    static int bookCount = 0;

    // history.dat fields
    static String[] histTimestamps  = new String[MAX_HISTORY];
    static String[] histRegNos      = new String[MAX_HISTORY];
    static String[] histActions     = new String[MAX_HISTORY];   // "issue", "return", "overdue"
    static String[] histBookIDs     = new String[MAX_HISTORY];
    static String[] histDueDates    = new String[MAX_HISTORY];
    static String[] histReturnDates = new String[MAX_HISTORY];   // "-" if not returned yet
    static String[] histFines       = new String[MAX_HISTORY];
    static int historyCount = 0;

    public static void main(String[] args) {
        System.out.println("\t\t╔════════════════════════════╗");
        System.out.println("\t\t║        LIBRARY SYSTEM      ║");
        System.out.println("\t\t╚════════════════════════════╝");
        System.out.println("========================================================\n");
        System.out.println("       Developed by: Muzammil Ghaffar ,Ahsan Raza");

        System.out.println("========================================================\n\n");
        loadUsers();
        loadBooks();
        loadHistory();

        mainMenu();
    }

    // ──── 2. MAIN MENU ─────────────────────────────────────────────────────────
    static void mainMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Welcome to SP25-BDS batch Library Management System ===");
            System.out.println("1) Admin Login");
            System.out.println("2) Student Login");
            System.out.println("3) Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    adminLogin();
                    break;
                case 2:
                    studentLogin();
                    break;
                case 3:
                    saveAllData();
                    System.out.println("Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ──── 3. STUDENT LOGIN & MENU ──────
    static void studentLogin() {
        System.out.println("----------------------Student----------------------");
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Username: ");
        String identifier = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

        //  Find user index by username or regNo
        int idx = findUserIndexByUsername(identifier);
        if (idx == -1) {
            idx = findUserIndexByRegNo(identifier);
        }
        //Checking all the credentials matches
        if (idx == -1 || !passwords[idx].equals(pass) || !roles[idx].equals("student")) {
            System.out.println("Invalid login or not registered. Contact Admin.");
            return; // back to main menu
        }

        //Show the Student Menu, passing the student’s index in users[]
        showStudentMenu(idx);
    }

    static void showStudentMenu(int studentIdx) {
        Scanner sc = new Scanner(System.in);
        System.out.println("****Aleart Message***For Issuing or removing book follow our guide");
        System.out.println("STEP 1: View all books of library for issuing or returning  books using Option 1");
        System.out.println("STEP 2: Note the BOOK ID of your desired book");
        System.out.println("STEP 3: Come back to this menu");
        System.out.println("STEP 4: Select 'Issue a Book or return book ' again when ready");
        while (true) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1) Display All Books");
            System.out.println("2) Issue a Book");
            System.out.println("3) Return a Book");
            System.out.println("4) Display Available Books");
            System.out.println("5) Search Book");
            System.out.println("6) Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    libraryAllBooks();
                    break;
                case 2:
                    studentIssueBook(studentIdx);//this method uses the student’s index in users[]
                    break;
                case 3:
                    studentReturnBook(studentIdx);//also this method uses the student’s index in users[]also
                    break;
                case 4:
                    displayAllAvailableBooks();
                    break;
                case 5:
                    studentSearchBook();
                    break;
                case 6:
                    return; // back to main menu
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ──── STUDENT: DISPLAY ALL BOOKS ─────
    static void libraryAllBooks() {
        System.out.println("\n--- All Books in Library ---");
        if (bookCount == 0) {
            System.out.println("No books in the catalog.");
            return;
        }
        for (int i = 0; i < bookCount; i++) {
            System.out.printf("ID: %-20s | Title: %-30s | Author: %-30s | Cat: %-20s | Status: %-10s\n",
                    bookIDs[i], titles[i], authors[i], bookCategory[i], statuses[i]);
        }
    }

    // ──── STUDENT: SEARCH BOOK (same logic as adminSearchBook) ────────────
    static void studentSearchBook() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nSearch by (1) Title, (2) Author, (3) Category: ");
        int choice = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter search keyword: ");
        String keyword = sc.nextLine().toLowerCase();

        boolean found = false;
        for (int i = 0; i < bookCount; i++) {
            boolean matches = false;
            switch (choice) {
                case 1:
                    if (titles[i].toLowerCase().contains(keyword)) matches = true;
                    break;
                case 2:
                    if (authors[i].toLowerCase().contains(keyword)) matches = true;
                    break;
                case 3:
                    if (bookCategory[i].toLowerCase().contains(keyword)) matches = true;
                    break;
                default:
                    System.out.println("Invalid search choice.");
                    return;
            }
            if (matches) {
                System.out.printf("ID: %s | Title: %s | Author: %s | Cat: %s | Status: %s\n",
                        bookIDs[i], titles[i], authors[i], bookCategory[i], statuses[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No matching books found.");
        }
    }

    // ────  STUDENT: ISSUE A BOOK ──────
    static void studentIssueBook(int studentIdx) {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Book ID to issue: ");
        String bid = sc.nextLine();
        int bIdx = findBookIndexByID(bid);
        if (bIdx == -1) {
            System.out.println("Book ID not found.");
            return;
        }
        if (!statuses[bIdx].equals("available")) {
            System.out.println("Book is currently not available.");
            return;
        }

        //  Mark book as issued
        statuses[bIdx] = "issued";

        //  Compute issue date and due date (14 days later)
        String issueDate = getCurrentDate();           // e.g. "2025-06-03 "
        String dueDate   = addDaysToDate(getCurrentDate()); // e.g. "2025-06-17"

        // 3.3.3 Append to history[]
        histTimestamps[historyCount]  = issueDate;
        histRegNos[historyCount]      = regNos[studentIdx];
        histActions[historyCount]     = "issue";
        histBookIDs[historyCount]     = bid;
        histDueDates[historyCount]    = dueDate;
        histReturnDates[historyCount] = "-";
        histFines[historyCount]       = "0";
        historyCount++;

        System.out.println("Book issued.Issued date "+issueDate+" Due date: " + dueDate);
        System.out.println("Alert.Make sure to submit it on time .your fine will increase daily up tu 1O rupees after due date");
    }

    // ────  STUDENT: RETURN A BOOK ─────
    static void studentReturnBook(int studentIdx) {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Book ID to return: ");
        String bid = sc.nextLine();
        int bIdx = findBookIndexByID(bid);
        if (bIdx == -1) {
            System.out.println("Book ID not found.");
            return;
        }

        //  Verify that studentIdx actually has an “issue” record for this book, and it’s not returned yet
        boolean hasIssued = false;
        int histIdx = -1;
        for (int i = 0; i < historyCount; i++) {
            if (histRegNos[i].equals(regNos[studentIdx]) &&
                    histBookIDs[i].equals(bid) &&
                    histActions[i].equals("issue") &&
                    histReturnDates[i].equals("-")) {
                hasIssued = true;
                histIdx = i;
                break;
            }
        }
        if (!hasIssued) {
            System.out.println("You have not issued that book or it’s already returned.");
            return;
        }

        //  Update book status back to available
        statuses[bIdx] = "available";

        //  Calculate fine (if returned late)
        String today = getCurrentDate();             // "2025-06-17"
        String due   = histDueDates[histIdx];        // e.g. "2025-06-15"
        int fine = calculateFine(due, today);        // e.g. (days late × 10)

        //  Log this return as a new history entry
        histActions[historyCount]      = "return";
        histTimestamps[historyCount]   = getCurrentDate();
        histRegNos[historyCount]       = regNos[studentIdx];
        histBookIDs[historyCount]      = bid;
        histDueDates[historyCount]     = due;
        histReturnDates[historyCount]  = today;
        histFines[historyCount]        = String.valueOf(fine);
        historyCount++;

        // 3.4.5 Also update the original “issue” record:
        histReturnDates[histIdx] = today;
        histFines[histIdx]      = String.valueOf(fine);

        if (fine > 0) {
            System.out.println("Book returned late. Fine: " + fine + " PKR.");
        } else {
            System.out.println("Book returned on time. No fine.");
        }
    }

    // ────  HELPER for  method: CALCULATE FINE ─────
    static int calculateFine(String dueDate, String returnDate) {
        // Convert strings to LocalDate
        LocalDate due = LocalDate.parse(dueDate);
        LocalDate returned = LocalDate.parse(returnDate);

        //built in method used to  Calculate how many days late
        long daysLate = ChronoUnit.DAYS.between(due, returned);

        int fine = 0;

        // If returned late, loop over each day and add 10 PKR
        if (daysLate > 0) {
            for (int i = 1; i <= daysLate; i++) {
                fine += 10;  // Add 10 PKR per late day
            }
        }

        return fine;
    }


    // ────  HELPER for method: GET CURRENT DATE / TIME ────────
    //Built in methods used
    static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }



    static String addDaysToDate(String date ) {
        LocalDate d = LocalDate.parse(date);
        return d.plusDays(14).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    // ──── 4. “FIND” HELPERS ─────
    static int findUserIndexByUsername(String user) {
        for (int i = 0; i < userCount; i++) {
            if (usernames[i].equals(user)) {
                return i;
            }
        }
        return -1;
    }

    static int findUserIndexByRegNo(String r) {
        for (int i = 0; i < userCount; i++) {
            if (regNos[i].equals(r)) {
                return i;
            }
        }
        return -1;
    }

    static int findBookIndexByID(String id) {
        for (int i = 0; i < bookCount; i++) {
            if (bookIDs[i].equals(id)) {
                return i;
            }
        }
        return -1;
    }

    // ────  5.“DISPLAY AVAILABLE BOOKS” (reused by both Admin & Student) ──────
    static void displayAllAvailableBooks() {
        System.out.println("\n--- Available Books ---");
        boolean any = false;
        for (int i = 0; i < bookCount; i++) {
            if (statuses[i].equals("available")) {
                System.out.printf("ID: %s | Title: %s | Author: %s | Cat: %s\n",
                        bookIDs[i], titles[i], authors[i], bookCategory[i]);
                any = true;
            }
        }
        if (!any) {
            System.out.println("No books currently available.");
        }
    }

    // ──── 6. ADMIN LOGIN & MENU  ───
    static void adminLogin() {
        System.out.println("----------------------Admin----------------------");
        Scanner sc = new Scanner(System.in);
        System.out.print("\n Username: ");
        String user = sc.nextLine();
        System.out.print(" Password: ");
        String pass = sc.nextLine();

        int idx = findUserIndexByUsername(user);
        if (idx == -1 || !passwords[idx].equals(pass) || !roles[idx].equals("admin")) {
            System.out.println("Invalid admin credentials.");
            return;
        }
        showAdminMenu();
    }

    static void showAdminMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1) Add Book");
            System.out.println("2) Remove Book");
            System.out.println("3) Search Book");
            System.out.println("4) Add User");
            System.out.println("5) Remove User");
            System.out.println("6) View All Available Books");
            System.out.println("7) View History (All Transactions)");
            System.out.println("8) View Books by Category");
            System.out.println("9) Display all books");
            System.out.println("10) Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1: adminAddBook();         break;
                case 2: adminRemoveBook();      break;
                case 3: adminSearchBook();      break;
                case 4: adminAddUser();         break;
                case 5: adminRemoveUser();      break;
                case 6: displayAllAvailableBooks(); break;
                case 7: displayAllHistory();    break;
                case 8: adminViewBooksByCategory(); break;
                case 9:libraryAllBooks();break;
                case 10: return;  // back to main menu
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ──── 6.1 ADMIN: ADD BOOK ─────
    static void adminAddBook() {
        if (bookCount >= MAX_BOOKS) {
            System.out.println("Cannot add more books. Storage is full.");
            return;
        }
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter new Book ID (e.g. B0010): ");
        String id = sc.nextLine();
        // we can add validation for  duplicate ID it can be updated
        System.out.print("Enter Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Author: ");
        String author = sc.nextLine();
        System.out.print("Enter Category (e.g. PF, DataScience, English, Civic): ");
        String cat = sc.nextLine();

        bookIDs[bookCount]    = id;
        titles[bookCount]     = title;
        authors[bookCount]    = author;
        bookCategory[bookCount] = cat;
        statuses[bookCount]   = "available";
        bookCount++;
        System.out.println("Book added successfully!");
    }

    // ──── 6.2 ADMIN: REMOVE BOOK ─────
    static void adminRemoveBook() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Book ID to remove: ");

        String id = sc.nextLine();
        int idx = findBookIndexByID(id);
        if (idx == -1) {
            System.out.println("Book ID not found.");
            return;
        }
        for (int i = idx; i < bookCount - 1; i++) {
            bookIDs[i]    = bookIDs[i + 1];
            titles[i]     = titles[i + 1];
            authors[i]    = authors[i + 1];
            bookCategory[i] = bookCategory[i + 1];
            statuses[i]   = statuses[i + 1];
        }
        bookCount--;
        System.out.println("Book removed successfully!");
    }

    // ──── 6.3 ADMIN: SEARCH BOOK ─────
    static void adminSearchBook() {
        // (Same code as studentSearchBook)
        Scanner sc = new Scanner(System.in);
        System.out.print("\nSearch by (1) Title, (2) Author, (3) Category: ");
        int choice = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter search keyword: ");
        String keyword = sc.nextLine().toLowerCase();

        boolean found = false;
        for (int i = 0; i < bookCount; i++) {
            boolean matches = false;
            switch (choice) {
                case 1:
                    if (titles[i].toLowerCase().contains(keyword)) matches = true;
                    break;
                case 2:
                    if (authors[i].toLowerCase().contains(keyword)) matches = true;
                    break;
                case 3:
                    if (bookCategory[i].toLowerCase().contains(keyword)) matches = true;
                    break;
                default:
                    System.out.println("Invalid search choice.");
                    return;
            }
            if (matches) {
                System.out.printf("ID: %s | Title: %s | Author: %s | Cat: %s | Status: %s\n",
                        bookIDs[i], titles[i], authors[i], bookCategory[i], statuses[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No matching books found.");
        }
    }

    // ──── 6.4 ADMIN: ADD USER ────────
    static void adminAddUser() {
        if (userCount >= MAX_USERS) {
            System.out.println("User storage is full.");
            return;
        }
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Reg. No. (e.g. sp25-bds-10): ");
        String reg = sc.nextLine();
        System.out.print("Enter Username  ");
        String uname = sc.nextLine();

        System.out.print("Enter Password (≤ 8 chars, must have 1 letter, 1 digit, 1 special): ");
        String pwd = sc.nextLine();
        if (!isValidPassword(pwd)) {
            System.out.println("Invalid password format.");
            return;
        }

        regNos[userCount]    = reg;
        usernames[userCount] = uname;
        passwords[userCount] = pwd;
        roles[userCount]     = "student";
        userCount++;
        System.out.println("Student user added successfully!");
    }

    static boolean isValidPassword(String s) {
        if (s.length() > 8) return false;
        boolean hasAlpha = false, hasDigit = false, hasSpecial = false;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c))      hasAlpha = true;
            else if (Character.isDigit(c))  hasDigit = true;
            else                            hasSpecial = true;
        }
        return hasAlpha && hasDigit && hasSpecial;
    }


    // ──── 6.5 ADMIN: REMOVE USER ───────
    static void adminRemoveUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Username to remove: ");
        String uname = sc.nextLine();
        int idx = findUserIndexByUsername(uname);
        if (idx == -1) {
            System.out.println("Username not found.");
            return;
        }
        for (int i = idx; i < userCount - 1; i++) {
            regNos[i]    = regNos[i + 1];
            usernames[i] = usernames[i + 1];
            passwords[i] = passwords[i + 1];
            roles[i]     = roles[i + 1];
        }
        userCount--;
        System.out.println("User removed successfully.");
    }

    // ──── 6.6 ADMIN: VIEW ALL HISTORY ─────
    static void displayAllHistory() {
        System.out.println("\n--- Full Transaction History ---");
        if (historyCount == 0) {
            System.out.println("No history records yet.");
            return;
        }
        for (int i = 0; i < historyCount; i++) {
            System.out.printf("Issue date: %-10s  | Registration number:%-10s  | Action:%-10s  |Book id: %-10s   | Due date:  %-10s| Return Date : %-10s|Fine %-10s \n",
                    histTimestamps[i], histRegNos[i], histActions[i],
                    histBookIDs[i], histDueDates[i], histReturnDates[i], histFines[i]);
        }
    }

    // ──── 6.7 ADMIN: VIEW BOOKS BY CATEGORY ─────
    static void adminViewBooksByCategory() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter category to view (e.g. PF, DataScience, English, Civic): ");
        String cat = sc.nextLine().toLowerCase();

        boolean found = false;
        for (int i = 0; i < bookCount; i++) {
            if (bookCategory[i].toLowerCase().equals(cat)) {
                System.out.printf("ID: %s | Title: %s | Author: %s | Status: %s\n",
                        bookIDs[i], titles[i], authors[i], statuses[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books found in that category.");
        }
    }

    // ──── 7. FILE I/O: LOAD AND SAVE ────────
    static void loadUsers() {
        try (Scanner sc = new Scanner(new FileReader("users.dat"))) {

            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split("\\|");
                if(parts.length<4){
                    continue;
                }

                regNos[userCount]    = parts[0];
                usernames[userCount] = parts[1];
                passwords[userCount] = parts[2];
                roles[userCount]     = parts[3];
                userCount++;
            }
        } catch (IOException e) {
            System.out.println("users.dat not found; starting with empty user list.");
        }
    }

    static void loadBooks() {
        try (Scanner sc = new Scanner(new FileReader("books.dat"))) {
            while ( sc.hasNextLine()) {
                String[] parts = sc.nextLine().split("\\|");
                if(parts.length<5){
                    continue;
                }

                bookIDs[bookCount]    = parts[0];
                titles[bookCount]     = parts[1];
                authors[bookCount]    = parts[2];
                bookCategory[bookCount] = parts[3];
                statuses[bookCount]   = parts[4];
                bookCount++;
            }
        } catch (IOException e) {
            System.out.println("books.dat not found; starting with empty book list.");
        }
    }

    static void loadHistory() {
        try (Scanner sc = new Scanner(new FileReader("history.dat"))) {

            while (sc.hasNext()) {
                String[] parts = sc.nextLine().split("\\|");
                if(parts.length<7){
                    continue;
                }
                histTimestamps[historyCount]  = parts[0];
                histRegNos[historyCount]      = parts[1];
                histActions[historyCount]     = parts[2];
                histBookIDs[historyCount]     = parts[3];
                histDueDates[historyCount]    = parts[4];
                histReturnDates[historyCount] = parts[5];
                histFines[historyCount]       = parts[6];
                historyCount++;
            }
        } catch (IOException e) {
            System.out.println("history.dat not found; starting with empty history.");
        }
    }

    static void saveAllData() {
        saveUsers();
        saveBooks();
        saveHistory();
    }

    static void saveUsers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("users.dat"))) {
            for (int i = 0; i < userCount; i++) {
                pw.write(regNos[i] + "|" + usernames[i] + "|" + passwords[i] + "|" + roles[i]);
                pw.println();
            }
        } catch (IOException e) {
            System.out.println("Error saving users.dat");
        }
    }

    static void saveBooks() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("books.dat"))) {
            for (int i = 0; i < bookCount; i++) {
                pw.write(bookIDs[i] + "|" + titles[i] + "|" + authors[i] + "|" + bookCategory[i] + "|" + statuses[i]);
                pw.println();
            }
        } catch (IOException e) {
            System.out.println("Error saving books.dat");
        }
    }

    static void saveHistory() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("history.dat"))) {
            for (int i = 0; i < historyCount; i++) {
                pw.println(histTimestamps[i] + "|" + histRegNos[i] + "|" + histActions[i] + "|" +
                        histBookIDs[i] + "|" + histDueDates[i] + "|" + histReturnDates[i] + "|" +
                        histFines[i]);
                pw.println();
            }
        } catch (IOException e) {
            System.out.println("Error saving history.dat");
        }
    }
}
