package ca.mcgill.ecse211.wallfollowing;
import lejos.hardware.Sound;

public class SoundPlayer extends Thread
{
   // IMPERIAL MARCH
   public static final String name1 = "Imperial March";
   public static final short[] note1 = { 0, 50, 1175, 40, 0, 13, 1175, 40, 0,
         13, 1175, 40, 0, 13, 936, 27, 0, 13, 1400, 13, 1175, 40, 0, 13, 936,
         27, 0, 13, 1397, 13, 1175, 80, 0, 27, 1760, 40, 0, 13, 1760, 40, 0,
         13, 1760, 40, 0, 13, 1864, 27, 0, 13, 1400, 13, 1112, 40, 0, 13, 819,
         27, 0, 13, 1400, 13, 1175, 80, 0, 27, 2352, 40, 0, 13, 1175, 27, 0,
         13, 1175, 13, 2352, 27, 0, 27, 2216, 27, 0, 13, 2096, 13, 1976, 13,
         1864, 13, 1976, 27, 0, 27, 1248, 13, 0, 13, 1664, 27, 0, 27, 1568, 27,
         0, 13, 1480, 13, 1400, 13, 1320, 13, 1400, 27, 0, 27, 936, 13, 0, 13,
         1112, 27, 0, 27, 936, 27, 0, 13, 1112, 13, 1400, 40, 0, 13, 1175, 27,
         0, 13, 1400, 13, 1760, 80, 0, 27, 2352, 40, 0, 13, 1175, 27, 0, 13,
         1175, 13, 2352, 27, 0, 27, 2216, 27, 0, 13, 2096, 13, 1976, 13, 1864,
         13, 1976, 27, 0, 27, 1248, 13, 0, 13, 1664, 27, 0, 27, 1568, 27, 0,
         13, 1480, 13, 1400, 13, 1320, 13, 1400, 27, 0, 27, 936, 13, 0, 13,
         1112, 27, 0, 27, 936, 27, 0, 13, 1400, 13, 1175, 40, 0, 13, 936, 27,
         0, 13, 1400, 13, 1175, 80, };

   public static void main(String[] args)
   {
      while (true)
      {
         for (int i = 0; i < note1.length; i += 2)
         {
            final int tone = (int) note1[i];
            final int b = i + 1;
            final int length = 10 * note1[b];
            Sound.playTone(tone, length);
            try
            {
               Thread.sleep(length);
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }
         }
      }
   }
}