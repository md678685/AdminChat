/*
 * Copyright (C) 2013 AE97
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogue.adminchat.runnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import com.rogue.adminchat.AdminChat;

/**
 * Adopted from TotalPermissions
 *
 * @since 1.2.0
 * @author Lord_Ralex
 * @version 1.2.1
 */
public class UpdateRunnable implements Runnable {

    private static final String VERSION_URL = "https://raw.github.com/1Rogue/AdminChat/master/VERSION";
    private Boolean isLatest = null;
    private String latest;
    private String version;
    private final AdminChat plugin;

    public UpdateRunnable(AdminChat p) {
        super();
        plugin = p;
        version = plugin.getDescription().getVersion();
    }

    public void run() {
        if (version.endsWith("SNAPSHOT") || version.endsWith("DEV")) {
            plugin.getLogger().warning("Dev build detected, update checking disabled");
            isLatest = true;
            return;
        }
        try {
            URL call = new URL(VERSION_URL);
            InputStream stream = call.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            latest = reader.readLine();
            reader.close();
            if (latest.equalsIgnoreCase(version)) {
                isLatest = true;
            } else {
                isLatest = false;
            }
            plugin.setUpdateStatus(!isLatest);
        } catch (MalformedURLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error checking for update");
            ex.printStackTrace();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error checking for update");
            ex.printStackTrace();
        }
    }
}