package com.example.aaa_magnumbread.chat.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.example.aaa_magnumbread.databinding.FragmentTutorialPageBinding

class TutorialPageFragment : Fragment() {
    private var _binding: FragmentTutorialPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorialPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgTutorial.setImageResource(requireArguments().getInt(ARG_ICON))
        binding.tvTutorialTitle.text = requireArguments().getString(ARG_TITLE)
        binding.tvTutorialDesc.text = requireArguments().getString(ARG_DESC)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ICON = "icon"
        private const val ARG_TITLE = "title"
        private const val ARG_DESC = "desc"

        fun newInstance(@DrawableRes icon: Int, title: String, desc: String) =
            TutorialPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ICON, icon)
                    putString(ARG_TITLE, title)
                    putString(ARG_DESC, desc)
                }
            }
    }
}