package pl.kkrpro.craftPayNotify.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.kkrpro.craftPayNotify.config.repository.CraftPayRepository;
import pl.kkrpro.craftPayNotify.utils.ChatUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Command(name = "itemshop")
@Permission("craftpay.notify.itemshop")
public class ItemShopCommand {

    private final CraftPayRepository repo;

    public ItemShopCommand(CraftPayRepository repo) {
        this.repo = repo;
    }

    @Execute(name = "create")
    void create(@Context CommandSender sender, @Arg String name, @Arg String... commandParts) {
        try {
            String command = String.join(" ", commandParts);

            repo.createItem(name, command);
            sender.sendMessage(ChatUtil.fixColor("&aDodano usługe: &e" + name));
        } catch (SQLException e) {
            sender.sendMessage(ChatUtil.fixColor("&cBłąd SQL"));
            e.printStackTrace();
        }
    }
    @Execute(name = "delete")
    void delete(@Context Player player, @Arg String name) {
        try {
            if (!repo.exists(name)) {
                player.sendMessage(ChatUtil.fixColor("&cTaka usługa nie istnieje."));
                return;
            }

            repo.deleteItem(name);
            player.sendMessage(ChatUtil.fixColor("&aUsunięto item: &e" + name));
        } catch (Exception e) {
            player.sendMessage(ChatUtil.fixColor("&cBłąd: " + e.getMessage()));
        }
    }

    @Execute
    void use(@Context Player player, @Arg String target , @Arg String itemName) {
        try {
            var itemOpt = repo.findByName(itemName);

            if (itemOpt.isEmpty()) {
                player.sendMessage(ChatUtil.fixColor("&cNie znaleziono itemu."));
                return;
            }
            var item = itemOpt.get();

            String cmd = item.command().replace("{player}", target);

            player.getServer().dispatchCommand(
                    player.getServer().getConsoleSender(), cmd
            );

            repo.logPurchase(player.getUniqueId().toString(), itemName);

            List<String> messages = new ArrayList<>();

            messages.add(ChatUtil.fixColor("&eItemshop"));
            messages.add(ChatUtil.fixColor("&aGracz &e" + target + " &azakupił usługe &e" + itemName));

            for (String msg : messages) {
                player.getServer().broadcastMessage(msg);
            }

            player.sendMessage(ChatUtil.fixColor("&aUżyto usługi: &e" + itemName));

        } catch (SQLException e) {
            player.sendMessage(ChatUtil.fixColor("&cBłąd SQL"));
            e.printStackTrace();
        }
    }
    @Execute(name = "edit")
    void edit(@Context Player player, @Arg String name, @Arg String... commandParts) {
        try {
            String command = String.join(" ", commandParts);

            repo.updateItem(name, command);

            player.sendMessage(ChatUtil.fixColor("&aZakutalizowano item: &e" + name));
        } catch (Exception e) {
            player.sendMessage(ChatUtil.fixColor("&cBłąd: " + e.getMessage()));
        }
    }
}
