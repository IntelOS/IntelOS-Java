/*
 * Copyright (c) 2024. Intel
 *
 * This file is part of LuminaryOS
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.luminary.os.commands.impl;

import com.luminary.os.OS;
import com.luminary.os.commands.Command;
import com.luminary.os.permissions.PermissionLevel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ExitCommand extends Command {
   public ExitCommand() {
      super("exit", OS.getLanguage().get("exitUsage"), Arrays.asList("ext", "shutdown"));
   }

   public void execute(List<String> args) {
      if(!args.isEmpty() && Objects.equals(args.get(0), "/?")) {
         System.out.println(this.getUsage());
         System.out.println(OS.getLanguage().get("aliases") + ": " + String.join(" ", this.getAliases()));
         return;
      }
      System.exit(0);
   }

   public PermissionLevel getPremission() {
      return PermissionLevel.USER;
   }
}
