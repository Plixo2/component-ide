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
}