/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        List<OpLady> opLadies = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        List<Person> cityA = new ArrayList<>();
        List<Person> cityB = new ArrayList<>();

        // Create OpLadies
        OpLady opLady1 = new OpLady(1);
        OpLady opLady2 = new OpLady(2);
        opLadies.add(opLady1);
        opLadies.add(opLady2);

        // Create Lines
        Line line1 = new Line(1);
        Line line2 = new Line(2);
        lines.add(line1);
        lines.add(line2);

        // Create contacts in city A
        for (int i = 1; i <= 6; i++) {
            Person person = new Person("A" + i);
            cityA.add(person);
        }

        // Create contacts in city B
        for (int i = 1; i <= 6; i++) {
            Person person = new Person("B" + i);
            cityB.add(person);
        }

        int threadCount = 2; // Number of threads to run at the same time
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        Lock opLadiesLock = new ReentrantLock();
        Lock linesLock = new ReentrantLock();

        Random random = new Random();

        int callCount = 0;
        int startIndex = random.nextInt(cityA.size()); // Random start index

        while (callCount < 36) {
            // Shuffle A city contacts
            Collections.shuffle(cityA);

            Person caller = cityA.get(startIndex); // Select the person to make the first call

            List<Person> calledList = new ArrayList<>(); // Created a list to keep track of already wanted contacts

            while (calledList.size() < 1) {
                
                List<Person> remainingCityA = new ArrayList<>(cityB.subList(0, startIndex));
                remainingCityA.addAll(cityB.subList(startIndex + 1, cityB.size()));
                Collections.shuffle(remainingCityA);

                Person recevier = remainingCityA.get(0);

                if (!calledList.contains(recevier)) {
                   // Create and execute the conversation task
                    executorService.execute(() -> {
                        OpLady selectedOpLady = null;
                        Line selectedLine = null;

                        opLadiesLock.lock();
                        try {
                            for (OpLady opLady : opLadies) {
                                if (opLady.isAvailable()) {
                                    selectedOpLady = opLady;
                                    break;
                                }
                            }
                        } finally {
                            opLadiesLock.unlock();
                        }

                        linesLock.lock();
                        try {
                            for (Line line : lines) {
                                if (line.isAvailable()) {
                                    selectedLine = line;
                                    break;
                                }
                            }
                        } finally {
                            linesLock.unlock();
                        }

                        if (selectedOpLady != null && selectedLine != null) {
                            selectedOpLady.setBusy(true);
                            selectedLine.setBusy(true);

                            System.out.println(selectedOpLady.getId() + ". OpLady: " + caller.getName() +
                                    " is calling " + recevier.getName() + " from " + selectedLine.getId() + ". line");

                            int randomDelay = random.nextInt(2000); // Random waiting time for the call duration
                            try {
                                Thread.sleep(randomDelay);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                           System.out.println(selectedOpLady.getId() + ". OpLady: Call between " + caller.getName() +
                                   " and " + recevier.getName() + " from " + selectedLine.getId() + ". line completed.");

                            selectedOpLady.setBusy(false);
                            selectedLine.setBusy(false);
                        }
                    });

                    calledList.add(recevier); // Add the called person to the list
                    callCount++;
                    if (callCount >= 36) {
                        break;
                    }
                }
            }

            
            if (startIndex >= cityA.size()) {
                startIndex = 0; // Initial index is reset
            }
        }

        executorService.shutdown(); // Close the thread pool
    }
}
