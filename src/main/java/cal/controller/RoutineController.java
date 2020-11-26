package cal.controller;

import cal.model.dto.RoutineDTO;
import cal.service.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/routine")
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    @PostMapping("/{userId}")
    public void create(@PathVariable final UUID userId, @RequestBody @Valid final RoutineDTO routineDTO){
        routineService.create(routineDTO,userId);
    }

    @GetMapping("/{userId}/{routineId}")
    public RoutineDTO find(@PathVariable final UUID routineId,@PathVariable final UUID userId){
        return routineService.find(routineId,userId);
    }

    @GetMapping("/{userId}")
    public List<RoutineDTO> findAll(@PathVariable final UUID userId){
        return routineService.findAll(userId);
    }

    @PutMapping("/{userId}")
    public RoutineDTO update(@RequestBody @Valid RoutineDTO routineDTO,@PathVariable final UUID userId){
        return routineService.update(routineDTO,userId);
    }

    @DeleteMapping("/{userId}/{routineId}")
    public void delete(@PathVariable final UUID userId, @PathVariable final UUID routineId){
        routineService.delete(routineId,userId);
    }
}
