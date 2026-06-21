import React from "react";

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "danger";
  size?: "sm" | "md" | "lg";
}

export const Button: React.FC<ButtonProps> = ({ 
  children, 
  variant = "primary", 
  size = "md",
  className = "", 
  ...props 
}) => {
  const baseStyles = "rounded-[6px] font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-primary disabled:opacity-50 disabled:cursor-not-allowed";
  
  const sizes = {
    sm: "px-3 py-1.5 text-xs",
    md: "px-6 py-2 text-sm",
    lg: "px-8 py-3 text-base",
  };
  
  const variants = {
    primary: "bg-primary text-white hover:bg-primary-dark",
    secondary: "bg-background border border-primary text-primary hover:bg-primary-light",
    danger: "bg-error text-white hover:bg-[#D93025]", // slightly darker red for hover
  };

  return (
    <button 
      className={`${baseStyles} ${sizes[size]} ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};
