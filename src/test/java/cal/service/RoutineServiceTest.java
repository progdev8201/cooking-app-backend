package cal.service;

import cal.model.dto.RoutineDTO;
import cal.model.entity.Article;
import cal.model.entity.Routine;
import cal.model.entity.RoutineArticle;
import cal.model.entity.User;
import cal.model.enums.ArticleCategorie;
import cal.model.enums.ArticleType;
import cal.repository.UserRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class RoutineServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createRoutineTest() {
        //ARRANGE
        User user = userRepository.save(setUpUser());

        int userRoutinesQty = user.getRoutines().size();

        RoutineService routineService = new RoutineService(userRepository);

        RoutineDTO routineToCreate = new RoutineDTO(user.getRoutines().get(0));
        routineToCreate.setId(UUID.randomUUID());

        //ACT
        routineService.create(routineToCreate, user.getUniqueId());

        //ASSERT
        user = userRepository.findById(user.getUniqueId()).get();

        assertEquals(userRoutinesQty + 1, user.getRoutines().size());
    }

    @Order(3)
    @Test
    public void findAllTest() {
        //ARRANGE
        User user = userRepository.save(setUpUser());

        RoutineService routineService = new RoutineService(userRepository);

        //ACT
        List<RoutineDTO> routineDTOS = routineService.findAll(user.getUniqueId());

        //ASSERT
        assertTrue(routineDTOS.size() == user.getRoutines().size());
    }

    @Test
    public void update() {
        //ARRANGE
        User user = userRepository.save(setUpUser());

        int indexOfRoutine = 0;
        String routineName = "fekoum";

        RoutineDTO routineDTO = new RoutineDTO(user.getRoutines().get(indexOfRoutine));
        RoutineService routineService = new RoutineService(userRepository);


        //ACT
        routineDTO.setName(routineName);
        RoutineDTO routineDTOUpdated = routineService.update(routineDTO, user.getUniqueId());

        //ASSERT
        assertEquals(routineName, routineDTOUpdated.getName());
    }


    @Order(4)
    @Test
    public void delete() {
        //ARRANGE
        User user = userRepository.save(setUpUser());

        int amountOfRoutines = user.getRoutines().size();

        RoutineService routineService = new RoutineService(userRepository);

        //ACT
        routineService.delete(user.getRoutines().get(0).getId(), user.getUniqueId());

        //ASSERT
        user = userRepository.findById(user.getUniqueId()).get();
        assertEquals(amountOfRoutines, user.getRoutines().size() + 1);
    }

    private User setUpUser() {
        // set up user
        User user = new User(UUID.randomUUID(), "test2@mail.com", "test", "test", "test", "test");

        //set up liste de routine article

        //set up une liste de routine
        for (int i = 0; i < 10; i++) {
            Routine routine = new Routine(UUID.randomUUID(), "test", new ArrayList<>());
            for (int j = 0; j < 10; j++) {
                Article article = new Article(UUID.randomUUID(), "test", "test", 5.99f, "test", ArticleType.LIQUID, ArticleCategorie.DAIRIES);
                routine.getRoutineArticles().add(new RoutineArticle(UUID.randomUUID(), article, 5));
            }
            user.getRoutines().add(routine);
        }

        return user;
    }

}
