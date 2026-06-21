import React from "react";

interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: "success" | "warning" | "error" | "info" | "default";
}

export const Badge: React.FC<BadgeProps> = ({ 
  children, 
  variant = "default", 
  className = "", 
  ...props 
}) => {
  const variants = {
    success: "bg-success-bg text-success",
    warning: "bg-warning-bg text-warning",
    error: "bg-critical-bg text-error",
    info: "bg-info-bg text-info",
    default: "bg-surface-hover text-text-secondary",
  };

  return (
    <span 
      className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </span>
  );
};
