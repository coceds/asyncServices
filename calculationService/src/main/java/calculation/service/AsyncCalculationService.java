package calculation.service;


import com.google.common.util.concurrent.ListenableFuture;

import java.math.BigDecimal;

public interface AsyncCalculationService {

    ListenableFuture<BigDecimal> multipleByTwo(BigDecimal parameter);

    ListenableFuture<BigDecimal> multipleByThree(BigDecimal parameter);
}
