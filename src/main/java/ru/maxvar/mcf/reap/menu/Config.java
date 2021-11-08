package ru.maxvar.mcf.reap.menu;

public class Config {
    private Boolean enabled = true;
    private Boolean collectToInventory = true;
    private Boolean playSound = true;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean mustCollectToInventory() {
        return collectToInventory;
    }

    public void setCollectToInventory(final Boolean collectToInventory) {
        this.collectToInventory = collectToInventory;
    }

    public Boolean mustPlaySound() {
        return playSound;
    }

    public void setPlaySound(final Boolean playSound) {
        this.playSound = playSound;
    }
}
