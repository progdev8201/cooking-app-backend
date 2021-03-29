package cal.service;

import cal.model.dto.RoutineDTO;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static cal.utility.EntityGenerator.setUpUserWithLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Import(RoutineService.class)
public class RoutineServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoutineService routineService;

    private User user;

    @BeforeEach
    public void beforeEach(){
        user = userRepository.save(setUpUserWithLogic());
    }

    @Test
    public void createRoutineTest() {
        //ARRANGE
        int userRoutinesQty = user.getRoutines().size();

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

        //ACT
        List<RoutineDTO> routineDTOS = routineService.findAll(user.getUniqueId());

        //ASSERT
        assertTrue(routineDTOS.size() == user.getRoutines().size());
    }

    @Test
    public void update() {
        //ARRANGE
        int indexOfRoutine = 0;

        final String routineName = "fekoum";

        RoutineDTO routineDTO = new RoutineDTO(user.getRoutines().get(indexOfRoutine));

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
        final int amountOfRoutines = user.getRoutines().size();

        //ACT
        routineService.delete(user.getRoutines().get(0).getId(), user.getUniqueId());

        //ASSERT
        user = userRepository.findById(user.getUniqueId()).get();
        assertEquals(amountOfRoutines, user.getRoutines().size() + 1);
    }
}
