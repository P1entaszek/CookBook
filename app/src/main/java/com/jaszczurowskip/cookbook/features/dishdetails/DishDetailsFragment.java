package com.jaszczurowskip.cookbook.features.dishdetails;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.jaszczurowskip.cookbook.R;
import com.jaszczurowskip.cookbook.databinding.FragmentMealDetailsBinding;
import com.jaszczurowskip.cookbook.datasource.model.DishesApiModel;
import com.jaszczurowskip.cookbook.features.IngredientsRecyclerAdapter;
import com.jaszczurowskip.cookbook.features.dishdetails.mvp.DishDetailsMVP;
import com.jaszczurowskip.cookbook.features.dishdetails.mvp.DishDetailsPresenter;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class DishDetailsFragment extends Fragment implements DishDetailsMVP.View {
    private static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";
    private FragmentMealDetailsBinding fragmentMealDetailsBinding;
    private long dishId;
    private Sprite progressBar;
    private IngredientsRecyclerAdapter ingredientsRecyclerAdapter;
    private DishDetailsPresenter presenter;

    public DishDetailsFragment() {
        // Required empty public constructor
    }

    public static DishDetailsFragment newInstance(@NonNull final String dish) {
        DishDetailsFragment dishDetailsFragment = new DishDetailsFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_ID, dish);
        dishDetailsFragment.setArguments(args);
        return dishDetailsFragment;

    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.dishId = Long.parseLong(Objects.requireNonNull(Objects.requireNonNull(args).getString(EXTRA_ITEM_ID)));
    }

    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new DishDetailsPresenter();
        presenter.attach(this);
        presenter.initView();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        fragmentMealDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_meal_details, container, false);
        return fragmentMealDetailsBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.destroy();
    }

    @Override
    public void setupview() {
        progressBar = new FadingCircle();
        fragmentMealDetailsBinding.progressBar.setIndeterminateDrawable(progressBar);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        fragmentMealDetailsBinding.recyclerView.setLayoutManager(layoutManager);
        presenter.gotDishFromService(dishId);
    }

    @Override
    public void showProgressDialog() {
        fragmentMealDetailsBinding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgressDialog() {
        fragmentMealDetailsBinding.progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(final @Nullable String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayDish(final @NonNull DishesApiModel dishesApiModel) {
        ingredientsRecyclerAdapter = new IngredientsRecyclerAdapter(getContext(), dishesApiModel.getIngredients());
        fragmentMealDetailsBinding.recyclerView.setAdapter(ingredientsRecyclerAdapter);
        fragmentMealDetailsBinding.mealNameTv.setText(dishesApiModel.getName());
        fragmentMealDetailsBinding.mealDescriptionTv.setText(dishesApiModel.getRecipe());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        Glide.with(getContext())
                .load(dishesApiModel.getPicture())
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(45)))
                .into(fragmentMealDetailsBinding.mealImg);
    }
}
