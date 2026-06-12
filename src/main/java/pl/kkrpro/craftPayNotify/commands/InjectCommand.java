package pl.kkrpro.craftPayNotify.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import pl.kkrpro.craftPayNotify.config.repository.CraftPayRepository;
import pl.kkrpro.craftPayNotify.utils.ChatUtil;

@Command(name = "inject")
@Permission("craftpay.notify.inject")
public class InjectCommand {

    private final CraftPayRepository repo;

    public InjectCommand(CraftPayRepository repo) {
        this.repo = repo;
    }

    @Execute
    void executeInject(@Context CommandSender sender) {
        try {
            repo.inject();
            sender.sendMessage(ChatUtil.fixColor("&aPomyślnie zaimplementowano tabele do bazy danych!"));
        } catch (Exception e) {
            sender.sendMessage(ChatUtil.fixColor("&cBłąd podczas injectowania!"));
            e.printStackTrace();
        }
    }

}
