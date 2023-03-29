package org.fireflyest.essential.service;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.fireflyest.essential.data.Config;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class EssentialEconomy implements Economy {

    private EssentialService service;

    public EssentialEconomy(EssentialService service) {
        this.service = service;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return String.format("%.2f", amount);
    }

    @Override
    public String currencyNamePlural() {
        return Config.CURRENCY_NAME;
    }

    @Override
    public String currencyNameSingular() {
        return Config.CURRENCY_NAME;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return service.selectSteveByName(playerName) != null;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return service.selectSteveByUid(player.getUniqueId()) != null;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return service.selectSteveByName(playerName) != null;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return service.selectSteveByUid(player.getUniqueId()) != null;
    }

    @Override
    public double getBalance(String playerName) {
        return service.selectMoneyByName(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return service.selectMoney(player.getUniqueId());
    }

    @Override
    public double getBalance(String playerName, String world) {
        return service.selectMoneyByName(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return service.selectMoney(player.getUniqueId());
    }

    @Override
    public boolean has(String playerName, double amount) {
        return service.selectMoneyByName(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return service.selectMoney(player.getUniqueId()) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return this.has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return this.has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        double money = service.selectMoneyByName(playerName);
        if (money >= amount) {
            service.updateMoneyByName("-", amount, playerName);
            return new EconomyResponse(amount, money - amount, EconomyResponse.ResponseType.SUCCESS, "SUCCESS");
        } else {
            return new EconomyResponse(0, money, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "NOT_IMPLEMENTED");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        double money = service.selectMoney(player.getUniqueId());
        if (money >= amount) {
            service.updateMoney("-", amount, player.getUniqueId());
            return new EconomyResponse(amount, money - amount, EconomyResponse.ResponseType.SUCCESS, "SUCCESS");
        } else {
            return new EconomyResponse(0, money, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "NOT_IMPLEMENTED");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return this.withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        service.updateMoneyByName("+", amount, playerName);
        return new EconomyResponse(amount, this.getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "SUCCESS");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        service.updateMoney("+", amount, player.getUniqueId());
        return new EconomyResponse(amount, this.getBalance(player), EconomyResponse.ResponseType.SUCCESS, "SUCCESS");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createBank'");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createBank'");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteBank'");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankBalance'");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankHas'");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankWithdraw'");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bankDeposit'");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankOwner'");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankOwner'");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankMember'");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBankMember'");
    }

    @Override
    public List<String> getBanks() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBanks'");
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerAccount'");
    }
    
    
}
