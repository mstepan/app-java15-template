import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

public class Main {


    public static void main(String[] args) {

        String[] data = {"one", "two", "three"};

        VarHandle arrayOfStringsHandle = MethodHandles.arrayElementVarHandle(String[].class);

        if (arrayOfStringsHandle.compareAndSet(data, 2, "three", "new")) {
            System.out.println("Was changed");
        }

        System.out.println(Arrays.toString(data));


        System.out.println("Maine done...");
    }

}

