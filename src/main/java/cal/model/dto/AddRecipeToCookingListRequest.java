package cal.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AddRecipeToCookingListRequest {
    private LocalDate cookDate;
    private List<RecipeDTO> recipesToCook;
}
