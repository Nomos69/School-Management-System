package com.school.management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.management.R;
import com.school.management.models.Fee;
import com.school.management.utils.DateTimeUtils;

import java.util.List;

public class FeeAdapter extends RecyclerView.Adapter<FeeAdapter.FeeViewHolder> {
    private Context context;
    private List<Fee> feeList;
    private OnFeeClickListener onClickListener;

    public interface OnFeeClickListener {
        void onFeeClick(Fee fee);
    }

    public FeeAdapter(Context context, List<Fee> feeList, OnFeeClickListener onClickListener) {
        this.context = context;
        this.feeList = feeList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public FeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fee, parent, false);
        return new FeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeeViewHolder holder, int position) {
        Fee fee = feeList.get(position);
        holder.bind(fee);
    }

    @Override
    public int getItemCount() {
        return feeList.size();
    }

    public void updateList(List<Fee> newList) {
        this.feeList = newList;
        notifyDataSetChanged();
    }

    class FeeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentId, tvFeeType, tvAmount, tvStatus, tvDueDate;
        private ProgressBar progressBar;

        FeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvFeeType = itemView.findViewById(R.id.tvFeeType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            progressBar = itemView.findViewById(R.id.progressBar);

            itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onFeeClick(feeList.get(getAdapterPosition()));
                }
            });
        }

        void bind(Fee fee) {
            tvStudentId.setText("Student: " + fee.getStudentId());
            tvFeeType.setText("Type: " + fee.getFeeType());
            tvAmount.setText(String.format("Amount: ₹%.2f", fee.getTotalAmount()));
            tvDueDate.setText("Due: " + DateTimeUtils.formatDate(fee.getDueDate()));
            
            tvStatus.setText(fee.getStatus());
            int statusColor = getStatusColor(fee.getStatus());
            tvStatus.setTextColor(context.getResources().getColor(statusColor));

            double percentage = fee.getTotalAmount() > 0 ? (fee.getPaidAmount() / fee.getTotalAmount()) * 100 : 0;
            progressBar.setProgress((int) percentage);
        }

        private int getStatusColor(String status) {
            switch (status) {
                case "PAID":
                    return R.color.payment_paid;
                case "PARTIAL":
                    return R.color.payment_partial;
                case "PENDING":
                    return R.color.payment_pending;
                case "OVERDUE":
                    return R.color.payment_overdue;
                default:
                    return R.color.text_secondary;
            }
        }
    }
}
