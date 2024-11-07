package com.deliciouspizza.service;

public class ProductService {
    //Map<Status, Set<Product>> --> EnumMap ? --> repository
    //-> faster getActive, slower deactivate

    //Map<Product, Status> -> opposite

    //Map<Product, Status> active o(1)
    //Map<Product, Status> unactive o(1)    or Set<Products>

    //как ше активираш продукт

    //Product p1 = new Pizza(StatusProduct.ACTIVE, PizzaType.MARGHERITA, PizzaSize.LARGE);
    //Product p2 = new Pizza(StatusProduct.ACTIVE, PizzaType.MARGHERITA, PizzaSize.LARGE);

    //Product d1 = new Drink(...);
    //Product d2 = new Drink(...);

    //ProdRepository --> curData

    // getMenu || getActiveProducts --> unmodified(Set<Product>)
    // void activateProduct/deactivate(Product)
    // getAvailability(Product);

    // deactivate(p1);{
        // p1.inacvite();
        // active.add(p1),
        // unactive.remove();
    // }


    //? update()

    // loadProductFromJson --> repository
    // saveProductToJson  --> repo
    // CRUD operations


}
