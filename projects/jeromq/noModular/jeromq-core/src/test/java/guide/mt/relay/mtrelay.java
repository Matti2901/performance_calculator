package guide.mt.relay;

import org.zeromq.ZContext;

/**
 * Multithreaded relay
 */
public class mtrelay
{

    public static void main(String[] args) throws InterruptedException
    {
        try (ZContext context = new ZContext()) {
            //  Step 1 signals to step 2
            Thread step1 = new Step1(context);
            step1.start();

            //  Step 2 relays the signal from step 1 to step 3
            Thread step2 = new Step2(context);
            step2.start();

            //  Step 3 waits for signal from step 2
            Thread step3 = new Step3(context);
            step3.start();

            step1.join();
            step2.join();
            step3.join();

            System.out.println("Test successful!");
        }
    }
}
