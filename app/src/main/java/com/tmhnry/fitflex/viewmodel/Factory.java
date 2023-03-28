package com.tmhnry.fitflex.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class Factory<T> implements ViewModelProvider.Factory {
    public static <T extends ViewModel>
    T get(ViewModelStoreOwner owner,
          Product<T> product,
          Class<T> aClass) {
        return new ViewModelProvider(owner, new Factory<T>(product)).get(aClass);
    }

    Product<T> product;

    public Factory(Product<T> product) {
        this.product = product;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return product.onCreate();
    }

    public interface Product<T> {
        <T extends ViewModel> T onCreate();
    }
}
