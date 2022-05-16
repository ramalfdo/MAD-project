package com.example.onlineshop.firestore
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.onlineshop.Activities.Settings
import com.example.onlineshop.activity.activity.*
import com.example.onlineshop.activity.activity.ui.fregments.*
import com.example.onlineshop.models.*
import com.example.onlineshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {
    private val mFirestore=FirebaseFirestore.getInstance()

    fun registerUser(activity: Register, userInfo: User){
        mFirestore.collection(Constants.USERS)//collection name
            .document(userInfo.id)//document for user id
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener{
                    e-> activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while register the user.",e
                )
            }

    }

    fun getCurrentUserID():String{
        val currentUser= FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser!=null){
            currentUserID=currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity){
        mFirestore.collection((Constants.USERS))
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                Log.i(activity.javaClass.simpleName,document.toString())
                val user=document.toObject(User::class.java)!!

                val sharedPreferences=
                    activity.getSharedPreferences(
                        Constants.ONLINESHOP_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor=sharedPreferences.edit()
                //key for logged in user name
                //value
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName}${user.lastname}"
                )

                editor.apply()
                //pass results to the login activity
                when(activity){
                    is Login ->{
                        activity.userLoggedInSuccess(user)
                    }
                    is Settings ->{

                        activity.userDetailsSuccess(user)
                    }
                }

            }
            .addOnFailureListener{e->
                when(activity){//in here i change UserProfile as Login
                    is Login -> {
                        activity.hideProgressDialog()
                    }
                    is Settings ->{
                        activity.hideProgressDialog()
                    }
                    //  is BassBoost.Settings ->{
                    //     activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName,"error while user details.",e
                )
            }
    }

    fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String, Any>) {

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when(activity){//in here i change UserProfile as Login
                    is UserProfile -> {
                        activity.userProfileUpdateSuccess()
                    }
                    //  is BassBoost.Settings ->{
                    //     activity.hideProgressDialog()
                }
            }
            .addOnFailureListener{e->
                when(activity){//in here i change UserProfile as Login
                    is UserProfile -> {
                        activity.hideProgressDialog()
                    }
                    //  is BassBoost.Settings ->{
                    //     activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName,"error while user details.",e
                )
            }
    }

    fun uploadImageToCloudStorage(activity:Activity,imageFileURI: Uri?, imageType:String){

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(activity,imageFileURI
            )
        )
        sRef.putFile(imageFileURI!!).addOnSuccessListener {
                taskSnapshot->
            Log.e("Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri -> Log.e("Downloadable Image URL",uri.toString())

                    when(activity){
                        is UserProfile->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProduct->{

                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }
            .addOnFailureListener{ //hide the progress bar and print the error in log.
                    exception->
                when(activity){
                    is UserProfile->{
                        activity.hideProgressDialog()
                    }
                    is AddProduct->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadProductDetails(activity: AddProduct, productInfo: Product){
        mFirestore.collection(Constants.PRODUCTS)//collection name
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener{
                    e-> activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",e
                )
            }

    }

    fun getProductsList(fragment: Fragment){
        mFirestore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Product list",document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when(fragment){
                    is ProductsFragment ->{
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }
            }
    }

    fun getProductDetails(activity: ProductDetails, mProductId: String) {

        // The collection name for PRODUCTS
        mFirestore.collection(Constants.PRODUCTS)
            .document(mProductId)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the product details in the form of document.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val product = document.toObject(Product::class.java)
                if(product != null){
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
            }
    }

    fun addCartItems(activity: ProductDetails, addToCart: CartItem){
        mFirestore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener{
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating the document for cart item.", e)
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productId: String) {

        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {

                // TODO Step 4: Notify the success result to the base class.
                // START
                // Notify the success result to the base class.
                fragment.productDeleteSuccess()
                // END
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }

    fun getCartList(activity: Activity) {
        // The collection name for PRODUCTS
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of cart items in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Cart Items ArrayList.
                val list: ArrayList<CartItem> = ArrayList()

                // A for loop as per the list of documents to convert them into Cart Items ArrayList.
                for (i in document.documents) {

                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }
                when (activity) {
                    is CartList -> {
                        activity.successCartItemList(list)
                    }
                    is Checkout ->{
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error based on the activity instance.
                when (activity) {
                    is CartList -> {
                        activity.hideProgressDialog()
                    }
                    is Checkout -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }
    }

    fun updateAllDetails(activity: Checkout, cartList: ArrayList<CartItem>, order: Order) {
        val writeBatch = mFirestore.batch()
        // Prepare the sold product details
        for (cart in cartList) {

            val soldProduct = SoldProduct(
                cart.product_owner_id,
                cart.title,
                cart.price,
                cart.cart_quantity,
                cart.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )

            //        val documentReference = mFirestore.collection(Constants.SOLD_PRODUCTS)
            //            .document()
            //          writeBatch.set(documentReference, soldProduct)
            //      }

            // Here we will update the product stock in the products collection based to cart quantity.
            //      for (cart in cartList) {

            //     val productHashMap = HashMap<String, Any>()

            //    productHashMap[Constants.STOCK_QUANTITY] =
            //        (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFirestore.collection(Constants.SOLD_PRODUCTS)
                .document(cart.product_id)

            writeBatch.set(documentReference, soldProduct)
        }
        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFirestore.collection(Constants.CART_ITEMS)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }
        writeBatch.commit().addOnSuccessListener {

            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(
                activity.javaClass.simpleName,
                "Error while updating all the details after order placed.",
                e
            )
        }

    }

    fun getSoldProductsList(fragment:SoldProductFragment){

        mFirestore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                //get the list of sold products in the form of documents.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                // created a new instance for Sold Products ArrayList.
                val list: ArrayList<SoldProduct> = ArrayList()
                for (i in document.documents) {

                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id

                    list.add(soldProduct)
                }

                fragment.successSoldProductsList(list)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error.
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting the list of sold products.",
                    e
                )
            }
    }

    fun getMyOrdersList(fragment: OrdersFragment) {
        mFirestore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                //      Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id
                    list.add(orderItem)
                }
                fragment.populateOrdersListInUI(list)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }

    fun placeOrder(activity: Checkout,order: Order){
        mFirestore.collection(Constants.ORDERS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                // START
                // Here call a function of base activity for transferring the result to it.
                activity.orderPlacedSuccess()
                // END
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error.
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order.",
                    e
                )
            }
    }

    //address
    fun deleteAddress(activity: AddressList, addressId: String) {

        mFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }
    fun updateAddress(activity: AddEditAddress, addressInfo: Address, addressId: String) {

        mFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }
    fun getAddressesList(activity: AddressList) {
        // The collection name for PRODUCTS
        mFirestore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // created a new instance for address ArrayList.
                val addressList: ArrayList<Address> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }
                activity.successAddressListFromFirestore(addressList)
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error wh ile getting the address list.", e)
            }
    }
    fun addAddress(activity: AddEditAddress, addressInfo: Address) {

        // Collection name address.
        mFirestore.collection(Constants.ADDRESSES)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }
//address


    fun updateMyCart(context: Context,cart_id: String,itemHashMap: HashMap<String, Any>){
        mFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {

                when(context){
                    is CartList -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener{
                    e->
                when(context){
                    is CartList -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,"Error while update the cart items.",e
                )
            }
    }
    fun checkIfItemExistInCart(activity: ProductDetails, productId: String){
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID,productId)
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName,document.documents.toString())
                if (document.documents.size>0){
                    activity.productExistsInCart()
                }
                else{
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener{e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",e)
            }
    }
    fun removeItemFromCart(context: Context,cart_id: String){
        mFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when(context){
                    is CartList ->{
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener{
                    e->
                when(context){
                    is CartList -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,"Error while removing item from the cart list.",e
                )
            }
    }
    fun getAllProductsList(activity: Activity){
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->

                Log.e("Products List", document.documents.toString())
                val productList:ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productList.add(product)
                }
                when(activity){
                    is CartList ->{
                        activity.successProductListFromFireStore(productList)
                    }
                    is Checkout ->{
                        activity.successProductsListFromFireStore(productList)
                    }
                }

            }
            .addOnFailureListener{e ->
                when(activity){
                    is CartList ->{
                        activity.hideProgressDialog()
                    }
                    is Checkout ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e("Get product list","Error while getting all product list.",e)
            }
    }
    fun getDashboardItemsList(fragment: DashboardFragment){
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName,document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }

                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener{
                    e-> fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName,
                    "Error while getting dashboard list.",e
                )
            }
    }
}