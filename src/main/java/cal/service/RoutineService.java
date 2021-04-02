package cal.service;

import cal.model.dto.RoutineDTO;
import cal.model.entity.Routine;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@Validated
public class RoutineService {

    @Autowired
    private UserRepository userRepository;

    private final Logger LOGGER = Logger.getLogger(RoutineService.class.getName());

    // SERVICES

    public void create(@Valid RoutineDTO routineDTO, @NotNull UUID userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.getRoutines().add(new Routine(routineDTO));
            userRepository.save(user);
            LOGGER.info("ROUTINE ADDED WITH SUCCESS");
            return;
        });

    }

    public RoutineDTO find(UUID routineId, @NotNull UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        List<RoutineDTO> routineDTOS = new ArrayList<>();

        user.ifPresent(u -> {
            Optional<Routine> routineToReturn = u.getRoutines().stream().filter(routine -> routine.getId().equals(routineId)).findFirst();
            routineToReturn.ifPresent(routine -> routineDTOS.add(new RoutineDTO(routine)));
        });

        return routineDTOS.size() > 0 ? routineDTOS.get(0) : null;
    }

    public List<RoutineDTO> findAll(@NotNull UUID userId) {
        List<RoutineDTO> routineDTOS = new ArrayList<>();

        userRepository.findById(userId).ifPresent(user -> {
            user.getRoutines().stream().forEach(routine -> {
                routineDTOS.add(new RoutineDTO(routine));
            });
        });

        return routineDTOS;
    }

    public RoutineDTO update(@Valid RoutineDTO routineDTO, @NotNull UUID userId) {
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {
            Optional<Routine> routineToUpdate = u.getRoutines().stream().filter(routine -> routine.getId().equals(routineDTO.getId())).findFirst();

            routineToUpdate.ifPresent(routine -> {
                //todo when adding the mapping method no need to look for index
                int index = u.getRoutines().indexOf(routine);
                u.getRoutines().set(index, new Routine(routineDTO));
                userRepository.save(u);
                LOGGER.info("THE ROUTINE HAS BEEN UPDATED SUCCESSFULLY");
            });
        });

        return find(routineDTO.getId(), userId);
    }

    public void delete(@NotNull UUID routineId, @NotNull UUID userId) {
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {
            Optional<Routine> routineToDelete = u.getRoutines().stream().filter(routine -> routine.getId().equals(routineId)).findFirst();

            routineToDelete.ifPresent(routine -> u.getRoutines().remove(routine));

            LOGGER.info("THE ROUTINE HAS BEEN DELETED SUCCESSFULLY");

        });

        userRepository.save(user.get());
    }
}
