package io.tesbo.report;

import java.io.IOException;
import java.nio.file.*;

public class TestngWatcher {

    public boolean checkFileChanged(String file)
    {
        boolean bool = false;
        final Path path = FileSystems.getDefault().getPath(file);

        try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
            final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            boolean startChecking = true;
            while (startChecking) {
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {

                    final Path changed = (Path) event.context();

                    if (changed.endsWith("testng-results.xml")) {

                        bool = true;
                        startChecking =false;
                    }
                }
                // reset the key
                boolean valid = wk.reset();
                if (!valid) {
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return  bool;
    }

}
