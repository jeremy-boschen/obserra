package org.newtco.obserra.backend.util;

public class Tuple {

    private Tuple() {
    }

    public static <T1, T2> Tuple2<T1, T2> of(T1 first, T2 second) {
        return new Tuple2<>(first, second);
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 first, T2 second, T3 third) {
        return new Tuple3<>(first, second, third);
    }

    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(T1 first, T2 second, T3 third, T4 fourth) {
        return new Tuple4<>(first, second, third, fourth);
    }


    public record Tuple2<T1, T2>(T1 first, T2 second) {
    }

    public record Tuple3<T1, T2, T3>(T1 first, T2 second, T3 third) {
    }

    public record Tuple4<T1, T2, T3, T4>(T1 first, T2 second, T3 third, T4 fourth) {
    }
}
