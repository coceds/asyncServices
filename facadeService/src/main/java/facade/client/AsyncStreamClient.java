package facade.client;


import rx.Observable;

public interface AsyncStreamClient {

    <T> Observable<T> createPost(CalculationEndPoints endPoint, Object request);
}
