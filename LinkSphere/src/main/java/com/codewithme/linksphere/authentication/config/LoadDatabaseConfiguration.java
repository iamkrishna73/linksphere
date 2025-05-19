package com.codewithme.linksphere.authentication.config;

import com.codewithme.linksphere.authentication.entities.AuthenticationUser;
import com.codewithme.linksphere.authentication.repositories.UserRepository;
import com.codewithme.linksphere.feed.entities.Post;
import com.codewithme.linksphere.feed.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Configuration
public class LoadDatabaseConfiguration {
    private static final int NUM_USERS = 500;
    private static final int MIN_POSTS_PER_USER = 1;
    private static final int MAX_POSTS_PER_USER = 3;
    private final PasswordEncoder encoder;
    private final Random random = new Random();

    public LoadDatabaseConfiguration(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PostRepository postRepository
                                          ) {
        return args -> {
            postRepository.deleteAll(); // delete posts first (due to FK constraint)
            userRepository.deleteAll();
            List<AuthenticationUser> users = createUsers(userRepository);
            createPosts(postRepository, users);
        };
    }

    private List<AuthenticationUser> createUsers(UserRepository userRepository) {
        List<String> firstNames = Arrays.asList("John", "Jane", "Michael", "Emily", "David", "Sarah", "James", "Emma",
                "William", "Olivia", "Liam", "Ava", "Noah", "Isabella", "Ethan", "Sophia", "Mason", "Mia", "Lucas",
                "Charlotte",
                "Alexander", "Amelia", "Daniel", "Harper", "Joseph", "Evelyn", "Samuel", "Abigail", "Henry",
                "Elizabeth",
                "Sebastian", "Sofia", "Jack", "Avery", "Owen", "Ella", "Gabriel", "Madison", "Matthew", "Scarlett",
                "Moussa", "Fatou", "Amadou", "Aisha", "Omar", "Aminata", "Ibrahim", "Mariam", "Abdul", "Zainab",
                "Wei", "Xia", "Ming", "Lin", "Hui", "Yan", "Jie", "Ying", "Feng", "Hong",
                "Mohammed", "Fatima", "Ahmed", "Aisha", "Ali", "Zainab", "Hassan", "Mariam", "Hussein", "Amira");

        List<String> lastNames = Arrays.asList("Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
                "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas",
                "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez",
                "Clark", "Ramirez", "Lewis", "Robinson", "Walker", "Young", "Allen", "King", "Wright", "Scott",
                "Diop", "Sow", "Fall", "Ndiaye", "Diallo", "Ba", "Sy", "Wade", "Gueye", "Mbaye",
                "Wang", "Li", "Zhang", "Liu", "Chen", "Yang", "Huang", "Zhou", "Wu", "Xu",
                "Al-Sayed", "Khan", "Ahmed", "Hassan", "Ali", "Ibrahim", "Rahman", "Sheikh", "Malik", "Qureshi");

        List<String> companies = Arrays.asList("Google", "Microsoft", "Apple", "Amazon", "Meta", "Netflix", "Tesla",
                "Adobe", "Twitter", "LinkedIn", "Spotify", "Uber", "Airbnb", "Salesforce", "Oracle", "IBM", "Intel",
                "Samsung", "Sony", "Docker", "Zoom", "Slack", "GitHub", "GitLab", "Redis", "MongoDB", "Orange",
                "Thales", "Capgemini", "Botify", "Bwat", "EDF", "Algolia", "Zoho", "Shopopop", "Société Générale",
                "BnpParibas", "Nexitis");

        List<String> positions = Arrays.asList("Software Engineer", "Data Scientist", "Product Manager",
                "DevOps Engineer", "HR Manager", "Full Stack Developer", "Frontend Developer", "Backend Developer",
                "Machine Learning Engineer", "Cloud Architect", "System Administrator", "Database Administrator",
                "Security Engineer", "QA Engineer", "Technical Lead", "Engineering Manager", "CTO", "VP of Engineering",
                "Solutions Architect", "Technical Project Manager");

        List<String> locations = Arrays.asList(
                "San Francisco, US", "New York, US", "Seattle, US", "Boston, US", "Austin, US",
                "London, UK", "Berlin, DE", "Paris, FR", "Amsterdam, NL", "Stockholm, SE",
                "Tokyo, JP", "Singapore, SG", "Sydney, AU", "Toronto, CA", "Vancouver, CA",
                "Dubai, AE", "Dakar, SN", "Bangalore, IN", "Seoul, KR", "Cape Town, ZA",
                "Mumbai, IN", "Shanghai, CN", "São Paulo, BR", "Mexico City, MX", "Dublin, IE");

        Set<String> emailSet = new HashSet<>();
        List<AuthenticationUser> users = new ArrayList<>();

        int i = 0;
        while (users.size() < NUM_USERS) {
            String firstName = firstNames.get(random.nextInt(firstNames.size()));
            String lastName = lastNames.get(random.nextInt(lastNames.size()));
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@example.com";

            if (!emailSet.add(email)) {
                i++;
                continue; // skip duplicates
            }

            String position = positions.get(random.nextInt(positions.size()));
            String company = companies.get(random.nextInt(companies.size()));
            String location = locations.get(random.nextInt(locations.size()));

            users.add(createUser(email, lastName, firstName, lastName, position, company, location, null));
            i++;
        }

        // Add 3 hardcoded unique users (skip emailSet check assuming these are guaranteed unique)
        users.addAll(List.of(
                createUser("john.doe@example.com", "john", "John", "Doe",
                        positions.get(random.nextInt(positions.size())),
                        companies.get(random.nextInt(companies.size())),
                        locations.get(random.nextInt(locations.size())),
                        null),
                createUser("anne.claire@example.com", "anne", "Anne", "Claire",
                        positions.get(random.nextInt(positions.size())),
                        companies.get(random.nextInt(companies.size())),
                        locations.get(random.nextInt(locations.size())),
                        null),
                createUser("arnauld.manner@example.com", "arnauld", "Arnauld", "Manner",
                        positions.get(random.nextInt(positions.size())),
                        companies.get(random.nextInt(companies.size())),
                        locations.get(random.nextInt(locations.size())),
                        null)
        ));

        return userRepository.saveAll(users);
    }



    private void createPosts(PostRepository postRepository, List<AuthenticationUser> users) {
        List<String> postTemplates = Arrays.asList(
                "Excited to share that %s just launched a new feature!",
                "Great discussion about %s at today's team meeting.",
                "Looking forward to the upcoming %s conference!",
                "Just completed a certification in %s. Always learning!",
                "Proud to announce that our team at %s achieved a major milestone.",
                "Interesting article about the future of %s in tech.",
                "Sharing my thoughts on the latest developments in %s.",
                "Amazing workshop on %s today!",
                "Big announcement: We're hiring %s experts at %s!",
                "Reflecting on my journey as a %s at %s.",
                "Here's what I learned about %s this week.",
                "Exciting times ahead for %s technology!",
                "Just published an article about %s best practices.",
                "Grateful for the amazing %s team at %s.",
                "Innovation in %s is moving faster than ever!");

        List<String> topics = Arrays.asList("AI", "Machine Learning", "Cloud Computing", "DevOps", "Blockchain",
                "Cybersecurity", "Data Science", "IoT", "5G", "Quantum Computing", "AR/VR", "Digital Transformation",
                "Agile Development", "Remote Work", "Tech Leadership");

        for (AuthenticationUser user : users) {
            int numPosts = random.nextInt(MAX_POSTS_PER_USER - MIN_POSTS_PER_USER + 1) + MIN_POSTS_PER_USER;

            for (int i = 0; i < numPosts; i++) {
                String template = postTemplates.get(random.nextInt(postTemplates.size()));
                String topic = topics.get(random.nextInt(topics.size()));
                String content = String.format(template, topic, user.getCompany());

                Post post = new Post(content, user);
                post.setLikes(generateLikes(users, random));

                postRepository.save(post);
            }
        }
    }

    private HashSet<AuthenticationUser> generateLikes(List<AuthenticationUser> users, Random random) {
        HashSet<AuthenticationUser> likes = new HashSet<>();
        int maxLikes = Math.min(50, users.size() / 10); // Maximum 50 likes or 10% of users
        int likesCount = random.nextInt(maxLikes);

        while (likes.size() < likesCount) {
            likes.add(users.get(random.nextInt(users.size())));
        }

        return likes;
    }

    private AuthenticationUser createUser(String email, String password, String firstName, String lastName,
                            String position, String company, String location, String profilePicture) {
        AuthenticationUser user = new AuthenticationUser(email, encoder.encode(password));
        user.setEmailVerified(true);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPosition(position);
        user.setCompany(company);
        user.setLocation(location);
        user.setProfilePicture(profilePicture);
        return user;
    }


}