package xxs.common.module.utils.random;

import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;
import java.util.Random;

/*随机生成数字*/
public class RandomNumber {
    public static String getStringRandom(int count) {
        if (count <= 0) {
            return Integer.toString(0);
        }
        String str = RandomStringUtils.randomNumeric(count);
        return str;
    }
    /*范围*/
    public static String getStringRandomByDigit(int digit) {
        Random random = new Random();
        return RandomNumber.getStringRandom(random.nextInt(digit));
    }

    public static void main(String[] args) {
        System.out.println(getStringRandomByDigit(127));
    }

}
