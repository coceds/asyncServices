package calculation.service;


import calculation.dto.CalculationResponse;
import rx.Observable;

public interface ObservableService {

    Observable<CalculationResponse> getRandomStream();
}
