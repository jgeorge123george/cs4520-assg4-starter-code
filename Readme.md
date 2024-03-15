

1. Use Recyclerview, MVVM, livedata & constraint layouts wherever applicable.
   Recyclerview used in ProductListFragment.kt  #28
   MVVM - used in Login Module , LoginViewMode.kt , Auth.Kt, LoginFragment.kt
   livedata in LoginViewModel.kt 15
   constraint layout used in fragment_product_list.xml
2. Data should be loaded on when the user navigates to the Product list fragment. Load data using retrofit and coroutines.
   ProductListFragment.kt fetchProductData()
   ProductResponse.kt , ProductService.kt
3. For Loading Data
   1. Progress Bar
      ProductListFragment.kt  #80
   2. Save to Db
      ProductListFragment.kt #163
      ProductResponse.kt, ProductService.kt
   3. If api returns 0-zero results
      ProductListFragment.kt #155
4. Json can be repeated products
   ProductListFragment.kt filter #113
5. Products in Json can have empty , missing data
   ProductListFragment.kt filter #114
6. When offline, data should be loaded from DB
   ProductListFragment.kt # 106 - 194 to 224 

Implement pagination using optional page parameters. This can be applied to any assignment.
   ProductService.kt  #8
   ProductListFragment #108
   