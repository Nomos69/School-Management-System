package com.school.management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.management.R;
import com.school.management.models.LibraryBook;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private Context context;
    private List<LibraryBook> bookList;
    private OnBookClickListener onClickListener;

    public interface OnBookClickListener {
        void onBookClick(LibraryBook book);
    }

    public BookAdapter(Context context, List<LibraryBook> bookList, OnBookClickListener onClickListener) {
        this.context = context;
        this.bookList = bookList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        LibraryBook book = bookList.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void updateList(List<LibraryBook> newList) {
        this.bookList = newList;
        notifyDataSetChanged();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvAuthor, tvCategory, tvAvailability, tvCopies;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            tvCopies = itemView.findViewById(R.id.tvCopies);

            itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onBookClick(bookList.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (onClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    // Long click can be used for delete or other operations
                }
                return true;
            });
        }

        void bind(LibraryBook book) {
            tvTitle.setText(book.getTitle());
            tvAuthor.setText("By: " + book.getAuthor());
            tvCategory.setText("Category: " + book.getCategory());
            tvCopies.setText("Copies: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
            
            int availabilityColor = book.isAvailable() && book.getAvailableCopies() > 0 
                ? R.color.success 
                : R.color.error;
            String availabilityText = book.getAvailableCopies() > 0 ? "Available" : "Not Available";
            
            tvAvailability.setText(availabilityText);
            tvAvailability.setTextColor(context.getResources().getColor(availabilityColor));
        }
    }
}
