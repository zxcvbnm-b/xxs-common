package xxs.common.module.utils.other;

import java.text.SimpleDateFormat;

/**
 *身份证校验
 */
public class IdCardUtil {
    /**
     * 身份证前17位的系数  加权因子
     */
    private final static Integer[] QUOTIETY = new Integer[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    /**
     * 校验码
     */
    private final static String[] END_NUM = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};

    /**
     * 省市编码
     */
    public final static String[] provinceCode = new String[]{"11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71", "81", "82", "91"};

    /**
     * 校验合法性
     *
     * @param idCard 18位身份证号
     * @return
     */
    public static boolean validate(String idCard) {
        boolean flag = false;
        if (idCard.length() != 18 || !isNumeric(idCard.substring(0, 17)) || !isProvince(idCard.substring(0, 2)) || !isDate(idCard.substring(6, 14))) {
            return false;
        }
        //拿到前17位
        String endNum = getEndNum(idCard.substring(0, 17));
        if (endNum.equals(idCard.substring(17).toUpperCase())) {
            System.out.println(idCard.substring(17).toUpperCase());
            flag = true;
        }
        return flag;
    }

    /**
     * 产生最后的校验码
     *
     * @param idCard 前17位
     * @return
     */
    public static String getEndNum(String idCard) {
        //拿到17位
        char[] idCards = idCard.toCharArray();
        //总和
        int sum = 0;
        for (int i = 0; i < idCards.length; i++) {
            sum += Integer.parseInt(String.valueOf(idCards[i])) * QUOTIETY[i];
        }
        return END_NUM[sum % 11];
    }


    /**
     * 校验是否都是数字
     *
     * @param cs
     * @return
     */
    public static boolean isNumeric(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        } else {
            int sz = cs.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 校验是否在省市编码中
     *
     * @param province 身份证号的前2位
     * @return
     */
    public static boolean isProvince(String province) {
        for (String key : provinceCode) {
            if (key.equals(province)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验 中间部分的出生时间是否合法
     *
     * @param yyyyMMdd 年月日
     * @return
     */
    public static boolean isDate(String yyyyMMdd) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);
        try {
            sdf.parse(yyyyMMdd);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}