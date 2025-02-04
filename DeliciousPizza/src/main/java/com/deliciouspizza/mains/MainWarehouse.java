package com.deliciouspizza.mains;

import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.enums.PizzaSize;
import com.deliciouspizza.enums.PizzaType;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.repository.Warehouse;

public class MainWarehouse {

    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();

        Product pizza = new Pizza(PizzaType.MEAT_LOVERS, PizzaSize.LARGE);
//        Product drink = new Drink(DrinkType.COKE, DrinkVolume.GRANDE);

        //warehouse.addStock(pizza, 10);
        //warehouse.printStock();

    }

}
