package ua.hackhud.dreamFishing.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.util.MessageUtils;

import java.util.ArrayList;

public class DreamFishingCommand implements CommandExecutor {

    private final Main plugin;

    public DreamFishingCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            MessageUtils.sendError(sender, "У вас недостаточно прав!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(sender);
                break;

            case "add":
                handleAdd(sender, args);
                break;

            case "addrod":
                handleAddRod(sender, args);
                break;

            default:
                MessageUtils.sendError(sender, "Неизвестная команда! Используйте /dreamfishing для помощи.");
                break;
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        try {
            plugin.registerConfigs();
            MessageUtils.sendInfo(sender, "Плагин успешно перезагружен!");
        } catch (Exception e) {
            MessageUtils.sendError(sender, "Ошибка при перезагрузке плагина!");
            e.printStackTrace();
        }
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendError(sender, "Эта команда доступна только игрокам!");
            return;
        }

        if (args.length < 3) {
            MessageUtils.sendError(sender, "Использование: /dreamfishing add <dropTable> <weight>");
            return;
        }

        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();

        if (item == null || item.getType().name().equals("AIR")) {
            MessageUtils.sendError(sender, "Возьмите предмет в руку!");
            return;
        }

        String dropTable = args[1];

        double weight;
        try {
            weight = Double.parseDouble(args[2]);
            if (weight <= 0) {
                MessageUtils.sendError(sender, "Вес должен быть положительным числом!");
                return;
            }
        } catch (NumberFormatException e) {
            MessageUtils.sendError(sender, "Неверный формат веса! Используйте число (например: 10.5)");
            return;
        }

        try {
            plugin.getDropsManager().addItemToDropTable(
                    dropTable,
                    item.clone(),
                    weight,
                    new ArrayList<>()
            );
            MessageUtils.sendInfo(sender, String.format(
                    "Предмет %s добавлен в таблицу %s с весом %.2f!",
                    item.getType().name(),
                    dropTable,
                    weight
            ));
        } catch (Exception e) {
            MessageUtils.sendError(sender, "Ошибка при добавлении предмета!");
            e.printStackTrace();
        }
    }

    private void handleAddRod(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendError(sender, "Эта команда доступна только игрокам!");
            return;
        }

        if (args.length < 2) {
            MessageUtils.sendError(sender, "Использование: /dreamfishing addrod <название>");
            return;
        }

        Player player = (Player) sender;
        ItemStack rodItem = player.getItemInHand();

        if (rodItem == null || rodItem.getType().name().equals("AIR")) {
            MessageUtils.sendError(sender, "Возьмите удочку в руку!");
            return;
        }

        if (!rodItem.getType().name().toUpperCase().contains("FISHING_ROD")) {
            MessageUtils.sendError(sender, "Предмет должен быть удочкой!");
            return;
        }

        String rodKey = args[1];

        try {
            plugin.getRodConfigManager().addRodItemStack(rodKey, rodItem.clone());
            MessageUtils.sendInfo(sender, "Удочка '" + rodKey + "' успешно добавлена в конфиг!");
        } catch (Exception e) {
            MessageUtils.sendError(sender, "Ошибка при добавлении удочки в конфиг!");
            e.printStackTrace();
        }
    }


    private void sendHelp(CommandSender sender) {
        MessageUtils.sendRaw(sender, "");
        MessageUtils.sendRaw(sender, "&7>> DreamFishing - Команды:");
        MessageUtils.sendRaw(sender, "&e>> /dreamfishing reload - Перезагрузить плагин.");
        MessageUtils.sendRaw(sender, "&e>> /dreamfishing add <dropTable> <weight> - Добавить предмет в таблицу дропа.");
        MessageUtils.sendRaw(sender, "&e>> /dreamfishing addrod <name> - Добавить удочку для трансформации в конфиг.");
        MessageUtils.sendRaw(sender, "");
    }

}
