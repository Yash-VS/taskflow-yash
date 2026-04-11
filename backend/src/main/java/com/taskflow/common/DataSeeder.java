package com.taskflow.common;

import com.taskflow.project.model.Project;
import com.taskflow.task.model.Task;
import com.taskflow.task.model.TaskPriority;
import com.taskflow.task.model.TaskStatus;
import com.taskflow.user.model.User;
import com.taskflow.user.repository.UserRepository;
import com.taskflow.project.repository.ProjectRepository;
import com.taskflow.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            log.info("Old data present — wiping for fresh Zomato seed...");
            taskRepository.deleteAll();
            projectRepository.deleteAll();
            userRepository.deleteAll();
        }

        log.info("Seeding Zomato test data...");

        User owner = seedUser("Deepinder Goyal", "deepinder@zomato.com");
        
        List<User> users = new ArrayList<>();
        users.add(seedUser("Akriti Chopra", "akriti@zomato.com"));
        users.add(seedUser("Gunjan Patidar", "gunjan@zomato.com"));
        users.add(seedUser("Albinder Dhindsa", "albinder@blinkit.com"));
        users.add(seedUser("Rider Ram", "ram@zomato.com"));
        users.add(seedUser("Rider Shyam", "shyam@zomato.com"));
        users.add(seedUser("Chef Sanjeev", "sanjeev@restaurant.com"));
        users.add(seedUser("Support Agent 1", "support1@zomato.com"));
        users.add(seedUser("Support Agent 2", "support2@zomato.com"));
        users.add(seedUser("Marketing Lead", "marketing@zomato.com"));
        users.add(seedUser("QA Engineer", "qa@zomato.com"));
        users.add(owner); // Distribute tasks to owner too

        Project p1 = seedProject("Zomato Gold Overhaul", "Revamping the subscription model for Zomato Gold.", owner);
        Project p2 = seedProject("Blinkit Dark Store Expansion", "Adding 50 new dark stores in Tier 2 cities.", owner);
        Project p3 = seedProject("Hyperpure Supply Chain", "Optimizing fresh ingredient tracking for restaurants.", owner);

        seedZomatoTasks(p1, owner, users, new String[] {
            "Design new Gold badge",
            "Update subscription pricing logic",
            "Analyze churn rate of Gold users",
            "Contact top restaurants for exclusive Gold deals",
            "Fix payment gateway timeout issue",
            "Launch marketing campaign",
            "QA mobile app view for Gold members",
            "Write API documentation for Gold endpoints"
        });

        seedZomatoTasks(p2, owner, users, new String[] {
            "Scout properties in Jaipur",
            "Hire delivery partners for Pune",
            "Integrate inventory API with new warehouses",
            "Train store managers",
            "Optimize routing algorithm for 10-min delivery",
            "Procure deep freezers for 5 stores"
        });

        seedZomatoTasks(p3, owner, users, new String[] {
            "Onboard 10 organic farms",
            "Audit warehouse quality control pipeline",
            "Fix app crash on vendor dashboard",
            "Re-negotiate packaging costs",
            "Deploy AI demand forecasting model"
        });

        log.info("Zomato seed complete. Login with owner: deepinder@zomato.com / password123");
    }

    private User seedUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123"));
        return userRepository.save(user);
    }

    private Project seedProject(String name, String description, User owner) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setOwner(owner);
        return projectRepository.save(project);
    }

    private void seedZomatoTasks(Project project, User creator, List<User> users, String[] taskTitles) {
        TaskStatus[] statuses = {TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.DONE};
        TaskPriority[] priorities = {TaskPriority.LOW, TaskPriority.MEDIUM, TaskPriority.HIGH};
        
        for (int i = 0; i < taskTitles.length; i++) {
            User assignee = users.get(i % users.size());
            TaskStatus status = statuses[random.nextInt(statuses.length)];
            TaskPriority priority = priorities[random.nextInt(priorities.length)];
            
            Task t = new Task();
            t.setTitle(taskTitles[i]);
            t.setDescription("Auto-generated task definition for: " + taskTitles[i]);
            t.setStatus(status);
            t.setPriority(priority);
            t.setProject(project);
            t.setCreator(creator);
            t.setAssignee(assignee);
            t.setStoryPoints(random.nextInt(8) + 1);
            t.setDueDate(LocalDate.now().plusDays(random.nextInt(14)));
            taskRepository.save(t);
        }
    }
}
