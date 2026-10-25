package org.zlf.cinema.platfrom.util;
//工具类包名util
import java.util.Random;
/**
 * ID生成器
 */
public class IdGenerator {
    public static void main(String[] args) {
        for(int i=0;i<10;i++){
            String id=generateId(10);
            System.out.println(id);
        }
    }

    private static char[]characters={
            'A','B','C','D','E','F','G','H','I',
            'J','K','L','M','N','O','P','Q','R',
            'S','T','U','V','W','X','Y','Z','0',
            '1','2','3','4','5','6','7','8','9'
    };

    /**
     * 生成指定长度的ID
     * @param length
     * @return
     */
    public static String generateId(int length){
        Random r=new Random();
        StringBuilder builder=new StringBuilder("ZLF_");
        for(int i=0;i<length;i++){
            int index=r.nextInt(characters.length);
            builder.append(characters[index]);
        }
        return builder.toString();
    }
}
