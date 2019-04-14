package amb.sponge.plugin.listeners;

import amb.sponge.plugin.constant.PluginKey;
import amb.sponge.plugin.constant.TeleporterTypeEnum;
import amb.sponge.plugin.core.Teleporter;
import amb.sponge.plugin.service.TeleporterDataService;
import amb.sponge.plugin.service.TeleporterLogicService;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.item.HarvestingProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.Consumer;

import static amb.sponge.plugin.core.PluginCore.instance;

/**
 * 监听玩家点击书
 */
public class ClickBookListener implements Consumer<ClickInventoryEvent> {

    @Override
    public void accept(ClickInventoryEvent event) {
        ItemStackSnapshot item = event.getCursorTransaction().getDefault();
        if (!item.get(Keys.BOOK_AUTHOR).isPresent()
                || item.get(Keys.BOOK_AUTHOR).get().getContentVersion() != -1
                || !event.getCause().first(Player.class).isPresent()){
            return;
        }
        Player player = event.getCause().first(Player.class).get();
        if (event instanceof ClickInventoryEvent.Drop.Outside.Primary){
            // 鼠标左键拖拽出去
            // 删除传送点
            Teleporter teleporter = item.get(PluginKey.AMB_TELEPOTTER).get();
            if (teleporter.getType().equals(TeleporterTypeEnum.PlayerTp)){
                TeleporterDataService.delPlayerData(player, teleporter.getId());
                player.sendMessage(Text.of("传送点"+teleporter.getName()+"已删除"));
            }
        } else if (event instanceof ClickInventoryEvent.Primary){
            // 鼠标左键点击
            String buttonType = item.get(Keys.BOOK_AUTHOR).get().toPlain();
            if (buttonType.equals("AllowBeTp")){
                // 修改开关
                boolean tpSwitch = item.get(Keys.ITEM_LORE).get().get(1).equals("允许") ? false : true;
                TeleporterDataService.savePlayerData(player, tpSwitch, null);
                player.sendMessage(Text.of("[允许其他玩家传送至此]开关已设置为"+tpSwitch));
            }else if (buttonType.equals("AddTeleporter")){
                // 增加传送点
                TeleporterLogicService.AddTeleporter(player, item.get(Keys.WALKING_SPEED).get().doubleValue());
            }else if (buttonType.equals("GotoTeleporter")){
                // 点击传送点
                TeleporterLogicService.GotoTeleporter(player, item.get(PluginKey.AMB_TELEPOTTER).get());
            }
        }
    }

    @Override
    public Consumer<ClickInventoryEvent> andThen(Consumer<? super ClickInventoryEvent> after) {
        return null;
    }
}