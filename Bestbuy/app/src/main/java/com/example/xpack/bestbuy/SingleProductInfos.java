package com.example.xpack.bestbuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


public class SingleProductInfos extends AppCompatActivity {

    private String url,url3;
    private int page;
    private ViewPager pager;
    private ProgressDialog progress;
    private int startPosition;
    ArrayList<Products> produits = new ArrayList<Products>();
    pagerAdaper mAdapter ;
    int pos=0,offset;
    Toolbar toolbar;
    ImageView logo,share;
    String sorted="false",type="";
    SearchView search;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActionBar bar = getActionBar();
//        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#333")));
        setContentView(R.layout.single_product_infos);
        logo=(ImageView) findViewById(R.id.logo);
        toolbar =(Toolbar) findViewById(R.id.toolbar);

        Picasso.with(getApplicationContext())
                .load("http://www.userlogos.org/files/logos/mafi0z/BestBuy.png")
                .into(logo);



        Bundle args = getIntent().getExtras();
        mAdapter= new pagerAdaper(getSupportFragmentManager());
        progress = new ProgressDialog(this);
        page = args.getInt("page");
        url= args.getString("url");
        url3= args.getString("url3");
        offset=args.getInt("offset");
        sorted=args.getString("sorted");
        type=args.getString("type");

        startPosition = args.getInt("position");
        // produits= (ArrayList<Products>) args.getSerializable("produits");
        pager = (ViewPager) findViewById(R.id.pager);
        Fetcher fetcher = new Fetcher(url,url3);
        fetcher.execute();
        search=(SearchView) findViewById(R.id.searchView);


        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logo.setVisibility(View.GONE);

            }
        });
        //reafficher le logo quand on quite la recherche
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                logo.setVisibility(View.VISIBLE);
                return false;
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //si l'utilisateur tape un nom de produit a rechercher
                //appeler l'activité AllProducts qui a son tours s'occupe de chercher
                //le nom du produit tapé par l'utilisateur
                Intent intent=new Intent(SingleProductInfos.this,AllProducts.class);
                String ur="https://api.bestbuy.com/v1/products(search=";
                String word="";
                Boolean word_found=false;
                int i=0;
                while(i<query.length()){
                    Log.e("t", "the inde is  "+i );
                    while((i<query.length()) && (query.charAt(i)!=' ')){

                        word+=query.charAt(i);
                        word_found=true;
                        i++;
                        Log.e("t", "index:"+i+" word = "+word);
                        // Log.e("t", "the word is  "+word );
                    }

                    if(word_found){
                        word+=" ";
                        word_found=false;
                    }

                    i++;
                }
                ur+=word.replaceAll(" ","&search=")+"*)?format=json&shwo=all&pageSize=25&page=";


                //  intent.putExtra("url1","https://api.bestbuy.com/v1/products(name="+query+"*%7Csearch="+query+"*)?format=json&show=all&pageSize=25&page=");
                intent.putExtra("url1",ur);
                intent.putExtra("url2","&apiKey=tghcgc6qnf72tat8a5kbja9r");
                intent.putExtra("page",1);
                intent.putExtra("decalage",0);
                intent.putExtra("title",getResources().getString(R.string.search_results));
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {




            }

            @Override
            public void onPageSelected(int position) {
                pos=position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                    if (pos==produits.size()-1 && state==1){

                        produits = new ArrayList<Products>();
                        mAdapter.notifyDataSetChanged();
                        page++;
                        startPosition = pos+1;
                        Fetcher fetcher = new Fetcher(url,url3);
                        fetcher.execute();
                        pos++;
                        offset++;
                    }

               }




        });


    }


    public class Fetcher extends AsyncTask<Object, Object, ArrayList<Products>> {
        String url,url3;
        public Fetcher(String url,String url3){

            this.url=url;
            this.url3=url3;
        }

        public void onPreExecute() {

            progress.setCancelable(false);
            progress.setTitle("loading products");
            progress.show();

        }

        @Override
        protected ArrayList<Products> doInBackground(Object... params) {


            try {

                    for (int i = 1; i <= page; i++) {

                        Parser.getProducts(produits, this.url+i +this.url3);
                    }



            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(sorted.equals("true")){
                MergeSort m=new MergeSort(produits,type);
                m.sortGivenArray();
            }

            return produits;
        }

        @Override
        protected void onPostExecute(final ArrayList<Products> prods) {
            progress.hide();

            pager.setAdapter(mAdapter);

            pager.setCurrentItem(startPosition);



        }





    }

    public class pagerAdaper extends FragmentPagerAdapter {


        public pagerAdaper(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Products prod = produits.get(position);
            SingleProductFragment fragment = new SingleProductFragment();
            Bundle args = new Bundle();
            args.putString("name", prod.name);
            args.putString("largeImage", prod.largeImage);
            args.putString("salePrice", prod.salePrice);
            args.putString("salesEnd", prod.salesEnd);
            args.putString("ratingCount", prod.ratingCount);
            args.putString("ratings", prod.customerReview);
            args.putString("isAvailable", prod.isAvailable);
            args.putString("longDescription", prod.longDescription);
            args.putString("addToCartUrl", prod.addToCartUrl);
           /// args.putString("logo","http://logok.org/wp-content/uploads/2014/09/Best_Buy_Logo.png");



            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            return produits.size();
        }

    }
}
