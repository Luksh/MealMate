package np.com.ismt.laxman.mealmate.ui.recipes.add

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import np.com.ismt.laxman.mealmate.databinding.LayoutAddInstructionItemBinding

class AddInstructionAdapter(
    private val instructions: List<String>,
    private val onRemoveInstruction: (Int) ->Unit
): RecyclerView.Adapter<AddInstructionAdapter.AddInstructionViewHolder>() {

    inner class AddInstructionViewHolder(
        private val binding: LayoutAddInstructionItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        private var instruction = ""

        init {
            binding.tietAddInstruction.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    instruction = s.toString()
                    assignInstructionData()
                }
            })
        }

        fun bindView(instruction: String) {
            this.instruction = instruction

            binding.apply {
                tietAddInstruction.setText(instruction)

                ibRemoveInstruction.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onRemoveInstruction(position)
                    }
                }
            }
        }

        private fun assignInstructionData() {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                (instructions as MutableList<String>)[position] = instruction
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddInstructionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutAddInstructionItemBinding.inflate(layoutInflater, parent, false)
        return AddInstructionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return instructions.size
    }

    override fun onBindViewHolder(holder: AddInstructionViewHolder, position: Int) {
        holder.bindView(instructions[position])
    }
}
