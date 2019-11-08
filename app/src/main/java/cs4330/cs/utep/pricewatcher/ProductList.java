package cs4330.cs.utep.pricewatcher;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

public class ProductList extends ArrayAdapter<Product> {

    private List<Product> products;
    private PopupItemListener listener;

    public ProductList(Context context, List<Product> products) {
        super(context, -1, products);
        this.products = products;
        listener = (PopupItemListener) getContext();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View itemView = convertView != null ? convertView
                : LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_layout, parent, false);

        Product product = products.get(position);
        TextView view = itemView.findViewById(R.id.itemView);
            view.setText(product.getItem());
        view = itemView.findViewById(R.id.initialView);
            view.setText(String.valueOf(product.getInitialPrice()));

        view = itemView.findViewById(R.id.currentView);
            view.setText(String.valueOf(product.getCurrentPrice()));

        view = itemView.findViewById(R.id.changeView);
            view.setText(String.valueOf(product.getPercentChange()));

        view = itemView.findViewById(R.id.dateView);
            view.setText(product.getAddedDate());
        TextView changeView = itemView.findViewById(R.id.changeView);
        if (product.getPercentChange() < 0) {
            changeView.setTextColor(Color.GREEN);
        } else if (product.getPercentChange() > 0) {
            changeView.setTextColor(Color.RED);
        }

        itemView.setOnClickListener(view1 -> createPopupMenu(view1, position));
        return itemView;
    }

    private void createPopupMenu(View view, final int position) {
        PopupMenu menu = new PopupMenu(getContext(), view, Gravity.END);
        menu.inflate(R.menu.popup_menu);
        menu.show();
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete:
                    listener.deleteItem(position);
                    return true;
                case R.id.browse:
                    listener.displayWebsite(position);
                    return true;
                case R.id.edit:
                    listener.editItem(position);
                    return true;
                default:
                    return false;
            }
        });
    }


    public interface PopupItemListener {
        void deleteItem(int index);
        void editItem(int index);
        void displayWebsite(int index);
    }
}
