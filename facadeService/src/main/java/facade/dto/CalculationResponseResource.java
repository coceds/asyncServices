package facade.dto;


import org.springframework.hateoas.Resource;

public class CalculationResponseResource extends Resource<CalculationResponse> {

    public CalculationResponseResource(CalculationResponse calculationResponse) {
        super(calculationResponse);
    }
}
