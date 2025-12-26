import java.util.concurrent.atomic.AtomicInteger;
public class Main {
    private static final int FINISH_LINE = 10;
    private volatile boolean isRunning = true;
    private volatile String winner = null;

    private AtomicInteger chickenProgress = new AtomicInteger(0);
    private AtomicInteger eggProgress = new AtomicInteger(0);

    class AnimalThread extends Thread {
        private final String animalName;
        private final AtomicInteger myProgress;
        private final AtomicInteger opponentProgress;

        public AnimalThread(String name, int priority,
                            AtomicInteger myProgress,
                            AtomicInteger opponentProgress) {
            this.animalName = name;
            this.myProgress = myProgress;
            this.opponentProgress = opponentProgress;
            this.setPriority(priority);
        }

        @Override
        public void run() {
            while (isRunning && myProgress.get() < FINISH_LINE && winner == null) {
                // Увеличиваем прогресс
                myProgress.incrementAndGet();

                // Регулируем приоритет
                adjustPriority();

                System.out.printf("%s: я первый(ая)!(Приоритет: %d)\n",
                        animalName,
                        this.getPriority());

                // Проверяем финиш
                if (myProgress.get() >= FINISH_LINE && winner == null) {
                    declareWinner();
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void adjustPriority() {
            int my = myProgress.get();
            int opponent = opponentProgress.get();

            if (my < opponent) {
                this.setPriority(Thread.MAX_PRIORITY);
            } else if (my > opponent) {
                this.setPriority(Thread.MIN_PRIORITY);
            } else {
                this.setPriority(Thread.NORM_PRIORITY);
            }
        }

        private synchronized void declareWinner() {
            if (winner == null) {
                winner = animalName;
                isRunning = false;
                System.out.println("\n" + animalName + " появилось(ась) первым(ой)!");
            }
        }
    }

    public void startRace() {
        System.out.println("СПОР НАЧИНАЕТСЯ!");
        System.out.println("Дистанция: " + FINISH_LINE + " метров\n");

        AnimalThread chicken = new AnimalThread("Курица", Thread.NORM_PRIORITY,
                chickenProgress, eggProgress);
        AnimalThread egg = new AnimalThread("Яйцо", Thread.NORM_PRIORITY,
                eggProgress, chickenProgress);

        chicken.start();
        egg.start();

        try {
            chicken.join();
            egg.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    public static void main(String[] args) {
        new Main().startRace();
    }


    }
