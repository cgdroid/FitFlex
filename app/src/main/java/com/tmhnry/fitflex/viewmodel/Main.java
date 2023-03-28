package com.tmhnry.fitflex.viewmodel;

import androidx.lifecycle.ViewModelStoreOwner;

public class Main implements Factory.Product<Main.ViewModel> {

    public Main(){
    }

    public ViewModel getModel(ViewModelStoreOwner owner) {
        return Factory.get(owner, this, ViewModel.class);
    }

    @Override
    public <T extends androidx.lifecycle.ViewModel> T onCreate() {
        return (T) new ViewModel("");
    }

    public static class ViewModel extends androidx.lifecycle.ViewModel {
        String name;

        public ViewModel(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
