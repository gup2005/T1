package tin;


import tin.impl.ServiceHandler;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ServiceHandler h = new ServiceHandler();
                h.performOperation("x");
        Scanner s = new Scanner(System.in);
        s.nextLine();
    }
}
