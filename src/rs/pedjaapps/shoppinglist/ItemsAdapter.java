package rs.pedjaapps.shoppinglist;


import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public final class ItemsAdapter extends ArrayAdapter<ItemsEntry>
{

	private final int itemsItemLayoutResource;

	public ItemsAdapter(final Context context, final int itemsItemLayoutResource)
	{
		super(context, 0);
		this.itemsItemLayoutResource = itemsItemLayoutResource;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent)
	{

		final View view = getWorkingView(convertView);
		final ViewHolder viewHolder = getViewHolder(view);
		final ItemsEntry entry = getItem(position);

		if(entry.getDone()){
			viewHolder.nameView.setPaintFlags(viewHolder.nameView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			viewHolder.quantityView.setPaintFlags(viewHolder.nameView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			viewHolder.priceView.setPaintFlags(viewHolder.nameView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			
		}
		viewHolder.nameView.setText(entry.getName());
		viewHolder.quantityView.setText(entry.getQuantity()+entry.getUnit());
		viewHolder.priceView.setText(entry.getValue()+entry.getCurency());
		
		viewHolder.imageView.setImageURI(Uri.parse(entry.getImage()));

		return view;
	}

	private View getWorkingView(final View convertView)
	{
		View workingView = null;

		if (null == convertView)
		{
			final Context context = getContext();
			final LayoutInflater inflater = (LayoutInflater)context.getSystemService
			(Context.LAYOUT_INFLATER_SERVICE);

			workingView = inflater.inflate(itemsItemLayoutResource, null);
		}
		else
		{
			workingView = convertView;
		}

		return workingView;
	}

	private ViewHolder getViewHolder(final View workingView)
	{
		final Object tag = workingView.getTag();
		ViewHolder viewHolder = null;


		if (null == tag || !(tag instanceof ViewHolder))
		{
			viewHolder = new ViewHolder();

			viewHolder.nameView = (TextView) workingView.findViewById(R.id.name);
			viewHolder.quantityView = (TextView) workingView.findViewById(R.id.quantity);
			viewHolder.imageView = (ImageView) workingView.findViewById(R.id.image);
			viewHolder.priceView = (TextView) workingView.findViewById(R.id.price);
			
			workingView.setTag(viewHolder);

		}
		else
		{
			viewHolder = (ViewHolder) tag;
		}

		return viewHolder;
	}

	private class ViewHolder
	{
		public TextView nameView;
		public TextView quantityView;
		public ImageView imageView;
		public TextView priceView;

	}


}
