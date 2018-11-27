package com.jaszczurowskip.cookbook.features.mealdetails;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.jaszczurowskip.cookbook.R;
import com.jaszczurowskip.cookbook.databinding.FragmentMealDetailsBinding;
import com.jaszczurowskip.cookbook.datasource.model.DishesApiModel;
import com.jaszczurowskip.cookbook.datasource.model.IngredientApiModel;
import com.jaszczurowskip.cookbook.datasource.retrofit.ApiService;
import com.jaszczurowskip.cookbook.datasource.retrofit.RetrofitClient;
import com.jaszczurowskip.cookbook.utils.rx.AppSchedulersProvider;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MealDetailsFragment extends Fragment {
    private static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";
    private Retrofit retrofit;
    private ApiService apiService;
    private FragmentMealDetailsBinding fragmentMealDetailsBinding;
    private long dishId;
    private Sprite progressBar;

    public MealDetailsFragment() {
        // Required empty public constructor
    }

    public static MealDetailsFragment newInstance(final String dish) {
        MealDetailsFragment mealDetailsFragment = new MealDetailsFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_ID, dish);
        mealDetailsFragment.setArguments(args);
        return mealDetailsFragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.dishId = Long.parseLong(Objects.requireNonNull(Objects.requireNonNull(args).getString(EXTRA_ITEM_ID)));
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRetrofit();
        initView();
    }

    private void initView() {
        progressBar = new FadingCircle();
        fragmentMealDetailsBinding.progressBar.setIndeterminateDrawable(progressBar);
        fetchDataFromRemote();
    }

    private void initRetrofit() {
        retrofit = RetrofitClient.getRetrofitInstance();
        apiService = retrofit.create(ApiService.class);
    }

    private void fetchDataFromRemote(){
        apiService.getDish(dishId)
                .subscribeOn(AppSchedulersProvider.getInstance().io())
                .observeOn(AppSchedulersProvider.getInstance().ui())
                .subscribe(new Observer<DishesApiModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    //no-op
                    }

                    @Override
                    public void onNext(DishesApiModel dishesApiModel) {
                        displayData(dishesApiModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), R.string.Please_check_your_internet_connection, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        fragmentMealDetailsBinding.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void displayData(DishesApiModel dishesApiModel) {
        List<IngredientApiModel> ingredientList;
        IngredientsDetailsGridAdapter adapter;
        ingredientList = dishesApiModel.getIngredients();
        adapter = new IngredientsDetailsGridAdapter(getContext(), ingredientList);
        fragmentMealDetailsBinding.ingredientsGridview.setAdapter(adapter);
        fragmentMealDetailsBinding.mealNameTv.setText(dishesApiModel.getName());
        fragmentMealDetailsBinding.mealDescriptionTv.setText(dishesApiModel.getRecipe());
        Glide.with(getContext()).load(dishesApiModel.getPicture()).into(fragmentMealDetailsBinding.mealImg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMealDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_meal_details, container, false);
        return fragmentMealDetailsBinding.getRoot();
    }
}
