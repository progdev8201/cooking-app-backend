package cal.controller;

import cal.model.dto.FridgeDTO;
import cal.model.dto.RecipeDTO;
import cal.service.FridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/fridge")
public class FridgeController {

    @Autowired
    private FridgeService fridgeService;

    @GetMapping("/{userId}")
    public FridgeDTO find(@PathVariable UUID userId) {
        return fridgeService.find(userId);
    }

    @GetMapping("/findAll/{userId}")
    public List<RecipeDTO> findAllCookable(@PathVariable UUID userId) {
        return fridgeService.findAllCookable(userId);
    }

    @PutMapping("/{userId}")
    public FridgeDTO update(@PathVariable UUID userId, @RequestBody FridgeDTO fridgeDTO) {
        return fridgeService.update(userId, fridgeDTO);
    }

}
