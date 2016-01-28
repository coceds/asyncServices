package calculation.controller;

import calculation.dto.CalculationRequestDto;
import calculation.dto.CalculationResponseDto;
import calculation.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculationController {

    @Autowired
    private CalculationService calculationService;

    @RequestMapping(value = "/multipleByTwo", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public CalculationResponseDto multipleByTwo(@RequestBody CalculationRequestDto request) {
        return new CalculationResponseDto(calculationService.multipleByTwo(request.getParameter()));
    }

    @RequestMapping(value = "/multipleByThree", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public CalculationResponseDto multipleByThree(@RequestBody CalculationRequestDto request) {
        return new CalculationResponseDto(calculationService.multipleByThree(request.getParameter()));
    }
}
