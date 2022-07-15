package de.plixo.general;

public class Tuple<A, B> {
    public A first;
    public B second;

    public Tuple(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public Tuple() {

    }

    @Override
    public String toString() {
        return "{" + first + ", " + second + "}";
    }

    public static class Triple<A, B, C> {
        public A first;
        public B second;
        public C third;

        public Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public Triple() {

        }


        @Override
        public String toString() {
            return "{" + first + ", " + second + ", " + third + "}";
        }
    }
}