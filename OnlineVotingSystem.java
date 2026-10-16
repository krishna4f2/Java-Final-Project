import java.util.*;


// ---------- Custom Exception ----------
class VotingException extends Exception {
    public VotingException(String message) {
        super(message);
    }
}


class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }

    public boolean checkPassword(String pass) {
        return password.equals(pass);
    }
}


class Candidate {
    private int id;
    private String name;

    public Candidate(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}


public class OnlineVotingSystem {

    // Stores registered users: username -> User
    static Map<String, User> users = new HashMap<>();

    // Tracks whether a user has voted: username -> true/false
    static Map<String, Boolean> hasVoted = new HashMap<>();

    // Candidate votes: candidate name -> vote count
    static Map<String, Integer> candidateVotes = new LinkedHashMap<>();

    // List of candidates (used for numbering in the menu)
    static List<Candidate> candidates = new ArrayList<>();

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        
        addCandidate(1, "Arjun");
        addCandidate(2, "Priya");
        addCandidate(3, "Rahul");
        addCandidate(4, "Sneha");

        while (true) {
            System.out.println("\n===== ONLINE VOTING SYSTEM =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Display Final Result");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            int choice = readInt();
            switch (choice) {
                case 1: register(); break;
                case 2: login(); break;
                case 3: displayResult(); break;
                case 4:
                    System.out.println("Thank you! Exiting...");
                    return;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static void addCandidate(int id, String name) {
        candidates.add(new Candidate(id, name));
        candidateVotes.put(name, 0);
    }

    // Register 
    static void register() {
        System.out.print("Enter new username: ");
        String username = sc.nextLine().trim();
        try {
            if (username.isEmpty()) {
                throw new VotingException("Username cannot be empty.");
            }
            if (users.containsKey(username)) {
                throw new VotingException("Username already exists.");
            }
            System.out.print("Enter password: ");
            String password = sc.nextLine().trim();
            if (password.isEmpty()) {
                throw new VotingException("Password cannot be empty.");
            }
            users.put(username, new User(username, password));
            hasVoted.put(username, false);   // new user has not voted yet
            System.out.println("Registration successful!");
        } catch (VotingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Login 
    static void login() {
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();
        try {
            User user = users.get(username);
            if (user == null || !user.checkPassword(password)) {
                throw new VotingException("Invalid username or password.");
            }
            System.out.println("Login successful! Welcome, " + username);
            userMenu(username);
        } catch (VotingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // login 
    static void userMenu(String username) {
        while (true) {
            System.out.println("\n--- Voter Menu (" + username + ") ---");
            System.out.println("1. View Candidate List");
            System.out.println("2. Cast Vote");
            System.out.println("3. View Total Votes");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");

            int choice = readInt();
            switch (choice) {
                case 1: showCandidates(); break;
                case 2: castVote(username); break;
                case 3: viewTotalVotes(); break;
                case 4:
                    System.out.println("Logged out.");
                    return;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Candidate List 
    static void showCandidates() {
        System.out.println("\nCandidate List:");
        for (Candidate c : candidates) {
            System.out.println(c.getId() + ". " + c.getName());
        }
    }

    // Vote Casting 
    static void castVote(String username) {
        try {
            if (hasVoted.get(username)) {
                throw new VotingException("You have already voted! One vote per student.");
            }
            showCandidates();
            System.out.print("Enter candidate number to vote: ");
            int id = readInt();

            Candidate selected = null;
            for (Candidate c : candidates) {
                if (c.getId() == id) {
                    selected = c;
                    break;
                }
            }
            if (selected == null) {
                throw new VotingException("Invalid candidate number.");
            }

            // Add 1 vote and mark the user as voted
            candidateVotes.put(selected.getName(), candidateVotes.get(selected.getName()) + 1);
            hasVoted.put(username, true);
            System.out.println("Vote cast successfully for " + selected.getName() + "!");
        } catch (VotingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // View Total Votes 
    static void viewTotalVotes() {
        System.out.println("\nTotal Votes:");
        int total = 0;
        for (Map.Entry<String, Integer> entry : candidateVotes.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            total += entry.getValue();
        }
        System.out.println("Total votes cast: " + total);
    }

    // Result (Finalist + Runner-Up) 
    static void displayResult() {
        int total = 0;
        for (int v : candidateVotes.values()) {
            total += v;
        }
        if (total == 0) {
            System.out.println("No votes have been cast yet.");
            return;
        }

        // Sort candidates by votes (highest first)
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(candidateVotes.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        Map.Entry<String, Integer> first = sorted.get(0);
        Map.Entry<String, Integer> second = sorted.get(1);

        System.out.println("\n========== FINAL RESULT ==========");
        if (first.getValue().equals(second.getValue())) {
            System.out.println("It's a TIE between " + first.getKey() + " and "
                    + second.getKey() + " with " + first.getValue() + " votes each!");
        } else {
            System.out.println("WINNER (Finalist) : " + first.getKey()
                    + " with " + first.getValue() + " votes");
            System.out.println("RUNNER-UP         : " + second.getKey()
                    + " with " + second.getValue() + " votes");
        }
        System.out.println("==================================");
    }

    // ----------  input (exception handling) ----------
    static int readInt() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;   // treated as invalid choice
        }
    }
}