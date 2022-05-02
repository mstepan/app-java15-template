import com.max.app.hashing.UniversalHashRegular;

public class Main {


    public static void main(String[] args) {

        /*


             */
        int a = 8479606, b = 1167286, mod = 8;
        UniversalHashRegular<String> hashFunc = new UniversalHashRegular<>(a, b, mod);

        String str1 = "xnkgrutktnftorckabiy";
        String str2 = "ugbhesccghukuaoxhjzvmnfhgrbgnacjpywmfaxryvbfcvnkhajewjlojn";


        System.out.println(hashFunc.hash(str1));
        System.out.println(hashFunc.hash(str2));

        System.out.println(str1.hashCode());
        System.out.println(str2.hashCode());

        System.out.println("Maine done...");
    }

}

