package calculation.dto;


public class CalculationResponseDto {

    private Integer result;

    public CalculationResponseDto(Integer result) {
        this.result = result;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
